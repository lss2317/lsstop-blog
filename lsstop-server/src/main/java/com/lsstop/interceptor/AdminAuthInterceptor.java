package com.lsstop.interceptor;

import com.lsstop.common.Result;
import com.lsstop.constant.AuthConst;
import com.lsstop.enums.StatusEnum;
import com.lsstop.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 后台身份认证拦截器
 * <p>责任：验证 Token 有效性、校验 admin 来源
 *
 * @author lishusheng
 * @date 2026/01/03
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行OPTIONS预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = extractToken(request);

        if (token == null) {
            unauthorized(response);
            return false;
        }

        if (!jwtUtils.validateAccessToken(token)) {
            tokenExpired(response);
            return false;
        }

        String source = jwtUtils.getSource(token);
        // 后台接口必须使用admin token
        if (!AuthConst.SOURCE_ADMIN.equals(source)) {
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

    private void unauthorized(HttpServletResponse response) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(Result.failure(StatusEnum.TOKEN_EXPIRED).asJsonString());
    }

    private void tokenExpired(HttpServletResponse response) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(Result.failure(StatusEnum.TOKEN_EXPIRED).asJsonString());
    }

    private void forbidden(HttpServletResponse response) throws Exception {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(Result.failure(StatusEnum.NO_PERMISSION).asJsonString());
    }
}
