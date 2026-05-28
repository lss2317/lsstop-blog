package com.lsstop.interceptor;

import com.lsstop.common.Result;
import com.lsstop.enums.StatusEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 后台接口权限拦截器
 * <p>责任：基于接口权限表进行接口级权限校验
 * <p>执行顺序在 AdminAuthInterceptor 之后，依赖其设置的 userId 属性
 * <p>
 * TODO: 接口权限表实现后，在此处接入权限校验逻辑
 *
 * @author lishusheng
 * @date 2026/05/05
 */
@Slf4j
@Component
public class AdminPermissionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS 预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String userId = (String) request.getAttribute("userId");
        if (userId == null) {
            // 未经过认证拦截器，不应该到这里
            forbidden(response);
            return false;
        }

        // TODO: 接入接口权限表进行权限校验
        return true;
    }

    private void forbidden(HttpServletResponse response) throws Exception {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(Result.failure(StatusEnum.NO_PERMISSION).asJsonString());
    }
}
