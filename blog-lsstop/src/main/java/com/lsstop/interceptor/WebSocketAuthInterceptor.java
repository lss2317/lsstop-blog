package com.lsstop.interceptor;

import com.lsstop.constant.AuthConst;
import com.lsstop.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket握手认证拦截器
 *
 * @author lishusheng
 * @date 2026/04/04
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final JwtUtils jwtUtils;

    @Override
    public boolean beforeHandshake(@NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response,
                                   @NotNull WebSocketHandler wsHandler, @NotNull Map<String, Object> attributes) {
        String token = extractToken(request);
        if (token == null) {
            log.warn("WebSocket握手失败：token缺失");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
        if (!jwtUtils.validateAccessToken(token)) {
            log.warn("WebSocket握手失败：token无效或已过期");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
        String source = jwtUtils.getSource(token);
        if (!AuthConst.SOURCE_FRONT.equals(source)) {
            log.warn("WebSocket握手失败：非前台用户token, source={}", source);
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return false;
        }
        String userId = jwtUtils.getSubject(token);
        attributes.put("userId", userId);
        return true;
    }

    /**
     * 提取token：优先从请求头获取，兜底从URL参数获取
     */
    private String extractToken(ServerHttpRequest request) {
        String header = request.getHeaders().getFirst(AuthConst.AUTH_HEADER);
        if (header != null && header.startsWith(AuthConst.TOKEN_PREFIX)) {
            return header.substring(AuthConst.TOKEN_PREFIX.length());
        }
        if (request instanceof ServletServerHttpRequest servletRequest) {
            return servletRequest.getServletRequest().getParameter("token");
        }
        return null;
    }

    @Override
    public void afterHandshake(@NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response,
                               @NotNull WebSocketHandler wsHandler, Exception exception) {
        // 无需处理
    }
}
