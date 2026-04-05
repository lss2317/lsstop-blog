package com.lsstop.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.lsstop.constant.ChatConst;
import com.lsstop.constant.CommonConst;
import com.lsstop.domain.dto.ChatMessageDTO;
import com.lsstop.domain.entity.ChatMessageEntity;
import com.lsstop.domain.vo.ChatMessageVO;
import com.lsstop.domain.vo.UserInfoVO;
import com.lsstop.service.ChatMessageService;
import com.lsstop.service.UserService;
import com.lsstop.utils.IpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 聊天室WebSocket处理器
 *
 * @author lishusheng
 * @date 2026/04/04
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatMessageService chatMessageService;
    private final UserService userService;

    /**
     * 在线用户会话池（userId -> 该用户的所有session）
     */
    private static final Map<String, Set<WebSocketSession>> SESSION_POOL = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        String userId = getUserId(session);
        if (userId != null) {
            SESSION_POOL.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(session);
            log.info("用户 {} 连接聊天室，当前在线人数: {}", userId, SESSION_POOL.size());
            broadcastOnlineCount();
        }
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
        String userId = getUserId(session);
        if (userId == null) {
            return;
        }
        try {
            // 处理客户端心跳ping
            String payload = message.getPayload();
            if (ChatConst.WS_PING.equalsIgnoreCase(payload)) {
                synchronized (session) {
                    session.sendMessage(new TextMessage(ChatConst.WS_PONG));
                }
                return;
            }
            ChatMessageDTO dto = JSON.parseObject(payload, ChatMessageDTO.class);
            if (dto == null) {
                sendError(session, ChatConst.INVALID_MESSAGE_FORMAT);
                return;
            }
            // 校验内容
            boolean hasContent = StringUtils.isNotBlank(dto.getContent());
            boolean hasImages = dto.getImages() != null && !dto.getImages().isEmpty();
            if (!hasContent && !hasImages) {
                sendError(session, ChatConst.MESSAGE_CONTENT_EMPTY);
                return;
            }
            // 内容长度校验
            if (hasContent && dto.getContent().length() > ChatConst.MAX_CONTENT_LENGTH) {
                sendError(session, ChatConst.CONTENT_TOO_LONG);
                return;
            }
            // 图片数量校验
            if (hasImages && dto.getImages().size() > ChatConst.MAX_IMAGE_COUNT) {
                sendError(session, ChatConst.TOO_MANY_IMAGES);
                return;
            }

            // 获取IP信息
            String ipAddress = getIpAddress(session);
            String ipRegion = IpUtils.getIpLocation(ipAddress);

            // 持久化消息
            ChatMessageEntity chatMessage = ChatMessageEntity.builder()
                    .userId(userId)
                    .content(dto.getContent())
                    .images(hasImages ? JSON.toJSONString(dto.getImages()) : null)
                    .ipAddress(ipAddress)
                    .ipRegion(ipRegion)
                    .build();
            chatMessageService.insertMessage(chatMessage);

            // 查询用户信息
            UserInfoVO userInfo = userService.getUserProfile(userId);

            // 构造广播VO
            ChatMessageVO vo = new ChatMessageVO();
            vo.setId(chatMessage.getId());
            vo.setUserId(userId);
            vo.setNickname(userInfo.getNickname());
            vo.setAvatar(userInfo.getAvatar());
            vo.setContent(dto.getContent());
            vo.setImages(hasImages ? dto.getImages() : null);
            vo.setIpRegion(ipRegion);
            vo.setCreateTime(LocalDateTime.now());

            // 广播消息给所有在线用户
            broadcastMessage(vo);
        } catch (Exception e) {
            log.error("处理聊天消息异常, userId: {}", userId, e);
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        String userId = getUserId(session);
        if (userId != null) {
            removeSession(userId, session);
            log.info("用户 {} 断开聊天室，当前在线人数: {}", userId, SESSION_POOL.size());
            broadcastOnlineCount();
        }
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) {
        String userId = getUserId(session);
        if (userId != null) {
            removeSession(userId, session);
            broadcastOnlineCount();
        }
        log.warn("WebSocket传输异常, userId: {}", userId, exception);
    }

    /**
     * 广播在线用户数
     */
    private void broadcastOnlineCount() {
        JSONObject data = new JSONObject();
        data.put("type", ChatConst.WS_TYPE_ONLINE_USER_COUNT);
        data.put("data", SESSION_POOL.size());
        broadcast(data.toJSONString());
    }

    /**
     * 广播聊天消息
     */
    private void broadcastMessage(Object data) {
        JSONObject json = new JSONObject();
        json.put("type", ChatConst.WS_TYPE_MESSAGE);
        json.put("data", data);
        broadcast(json.toJSONString());
    }

    /**
     * 向指定session发送错误提示
     */
    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    private void sendError(WebSocketSession session, String msg) {
        JSONObject json = new JSONObject();
        json.put("type", ChatConst.WS_TYPE_ERROR);
        json.put("data", msg);
        try {
            synchronized (session) {
                session.sendMessage(new TextMessage(json.toJSONString()));
            }
        } catch (IOException e) {
            log.error("发送错误提示失败", e);
        }
    }

    /**
     * 向所有在线用户发送消息
     */
    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    private void broadcast(String message) {
        TextMessage textMessage = new TextMessage(message);
        SESSION_POOL.values().stream()
                .flatMap(Set::stream)
                .filter(WebSocketSession::isOpen)
                .forEach(session -> {
                    try {
                        synchronized (session) {
                            session.sendMessage(textMessage);
                        }
                    } catch (IOException e) {
                        log.error("广播消息失败", e);
                        String userId = getUserId(session);
                        if (userId != null) {
                            removeSession(userId, session);
                        }
                        try {
                            session.close();
                        } catch (IOException ignored) {
                        }
                    }
                });
    }

    /**
     * 移除用户的某个session，若该用户无session则原子移除整个key
     */
    private void removeSession(String userId, WebSocketSession session) {
        SESSION_POOL.computeIfPresent(userId, (key, sessions) -> {
            sessions.remove(session);
            return sessions.isEmpty() ? null : sessions;
        });
    }

    private String getUserId(WebSocketSession session) {
        return (String) session.getAttributes().get("userId");
    }

    private String getIpAddress(WebSocketSession session) {
        String ip = (String) session.getAttributes().get("ipAddress");
        return ip != null ? ip : CommonConst.UNKNOWN;
    }
}
