package com.lsstop.interceptor;

import com.lsstop.constant.AuthConst;
import com.lsstop.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 前台认证拦截器（拦截 /front/** 路径，必须登录且使用front token）
 *
 * @author lishusheng
 * @date 2026/01/03
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FrontAuthInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = extractToken(request);

        if (token == null) {
            unauthorized(response, "未登录或Token已过期");
            return false;
        }

        if (!jwtUtils.validateAccessToken(token)) {
            unauthorized(response, "Token无效或已过期");
            return false;
        }

        // 前台接口必须使用front token
        String source = jwtUtils.getSource(token);
        if (!AuthConst.SOURCE_FRONT.equals(source)) {
            forbidden(response);
            return false;
        }

        request.setAttribute("userId", jwtUtils.getSubject(token));
        request.setAttribute("source", source);
        return true;
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(AuthConst.AUTH_HEADER);
        if (header != null && header.startsWith(AuthConst.TOKEN_PREFIX)) {
            return header.substring(AuthConst.TOKEN_PREFIX.length());
        }
        return null;
    }

    private void unauthorized(HttpServletResponse response, String msg) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"msg\":\"" + msg + "\"}");
    }

    private void forbidden(HttpServletResponse response) throws Exception {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":403,\"msg\":\"" + "请使用前台账号访问" + "\"}");
    }
}
