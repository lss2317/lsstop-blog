package com.lsstop.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.lsstop.constant.ChatConst;
import com.lsstop.domain.dto.ChatMessageDTO;
import com.lsstop.domain.entity.ChatMessageEntity;
import com.lsstop.domain.vo.ChatMessageVO;
import com.lsstop.service.ChatMessageService;
import com.lsstop.utils.IpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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

    /**
     * 在线用户会话池（userId -> session）
     */
    private static final Map<String, WebSocketSession> SESSION_POOL = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = getUserId(session);
        if (userId != null) {
            SESSION_POOL.put(userId, session);
            log.info("用户 {} 连接聊天室，当前在线人数: {}", userId, SESSION_POOL.size());
            broadcastOnlineCount();
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String userId = getUserId(session);
        if (userId == null) {
            return;
        }
        try {
            ChatMessageDTO dto = JSON.parseObject(message.getPayload(), ChatMessageDTO.class);
            // 校验内容
            boolean hasContent = StringUtils.isNotBlank(dto.getContent());
            boolean hasImages = dto.getImages() != null && !dto.getImages().isEmpty();
            if (!hasContent && !hasImages) {
                return;
            }
            // 内容长度校验
            if (hasContent && dto.getContent().length() > ChatConst.MAX_CONTENT_LENGTH) {
                return;
            }
            // 图片数量校验
            if (hasImages && dto.getImages().size() > ChatConst.MAX_IMAGE_COUNT) {
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

            // 构造广播VO
            ChatMessageVO vo = new ChatMessageVO();
            vo.setId(chatMessage.getId());
            vo.setUserId(userId);
            vo.setContent(dto.getContent());
            vo.setImages(hasImages ? dto.getImages() : null);
            vo.setIpRegion(ipRegion);
            vo.setCreateTime(LocalDateTime.now());

            // 广播消息给所有在线用户
            broadcastMessage(ChatConst.WS_TYPE_MESSAGE, vo);
        } catch (Exception e) {
            log.error("处理聊天消息异常, userId: {}", userId, e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String userId = getUserId(session);
        if (userId != null) {
            SESSION_POOL.remove(userId);
            log.info("用户 {} 断开聊天室，当前在线人数: {}", userId, SESSION_POOL.size());
            broadcastOnlineCount();
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        String userId = getUserId(session);
        if (userId != null) {
            SESSION_POOL.remove(userId);
        }
        log.warn("WebSocket传输异常, userId: {}", userId, exception);
    }

    /**
     * 广播在线人数
     */
    private void broadcastOnlineCount() {
        JSONObject data = new JSONObject();
        data.put("type", ChatConst.WS_TYPE_ONLINE_COUNT);
        data.put("data", SESSION_POOL.size());
        broadcast(data.toJSONString());
    }

    /**
     * 广播消息
     */
    private void broadcastMessage(String type, Object data) {
        JSONObject json = new JSONObject();
        json.put("type", type);
        json.put("data", data);
        broadcast(json.toJSONString());
    }

    /**
     * 向所有在线用户发送消息
     */
    private void broadcast(String message) {
        TextMessage textMessage = new TextMessage(message);
        SESSION_POOL.values().forEach(session -> {
            if (session.isOpen()) {
                try {
                    synchronized (session) {
                        session.sendMessage(textMessage);
                    }
                } catch (IOException e) {
                    log.error("广播消息失败", e);
                }
            }
        });
    }

    private String getUserId(WebSocketSession session) {
        return (String) session.getAttributes().get("userId");
    }

    private String getIpAddress(WebSocketSession session) {
        if (session.getRemoteAddress() != null) {
            return session.getRemoteAddress().getAddress().getHostAddress();
        }
        return "unknown";
    }
}
