package com.lsstop.interceptor;

import com.lsstop.common.Result;
import com.lsstop.enums.StatusEnum;
import com.lsstop.service.MenuService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

/**
 * 后台接口权限拦截器
 * <p>责任：基于动态 URL 匹配的接口级权限校验
 * <p>执行顺序在 AdminAuthInterceptor 之后，依赖其设置的 userId 属性
 * <p>
 * 校验逻辑：
 * <ol>
 *     <li>用全局按钮权限规则判断当前接口是否受权限管控</li>
 *     <li>如果受管控，再判断用户是否拥有该接口的权限</li>
 *     <li>如果不受管控（没有任何规则匹配），说明是菜单级接口，放行</li>
 * </ol>
 *
 * @author lishusheng
 * @date 2026/05/05
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminPermissionInterceptor implements HandlerInterceptor {

    private final MenuService menuService;

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

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

        String method = request.getMethod();
        String uri = request.getRequestURI();

        // 第一步：用全局规则判断该接口是否受按钮权限管控
        Set<String> allPermissions = menuService.getAllApiPermissions();
        if (!isProtectedApi(allPermissions, method, uri)) {
            // 没有任何按钮权限规则管控该接口，属于菜单级接口，放行
            return true;
        }

        // 第二步：该接口受管控，判断用户是否有权限
        Set<String> userPermissions = menuService.getUserApiPermissions(userId);
        if (hasPermission(userPermissions, method, uri)) {
            return true;
        }

        // 用户没有该接口的权限
        forbidden(response);
        return false;
    }

    /**
     * 判断当前请求是否受按钮权限管控
     * <p>只要全局规则中有任意一条的 URI 模式匹配当前请求 URI（不考虑 Method），就算受管控
     */
    private boolean isProtectedApi(Set<String> allPermissions, String method, String uri) {
        for (String pattern : allPermissions) {
            String[] parts = pattern.split(":", 2);
            if (parts.length != 2) {
                continue;
            }
            String patternUri = parts[1];
            if (PATH_MATCHER.match(patternUri, uri)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断用户是否拥有当前接口的权限
     * <p>必须 Method + URI 双重匹配
     */
    private boolean hasPermission(Set<String> userPermissions, String method, String uri) {
        for (String pattern : userPermissions) {
            String[] parts = pattern.split(":", 2);
            if (parts.length != 2) {
                continue;
            }
            String patternMethod = parts[0];
            String patternUri = parts[1];
            if (patternMethod.equalsIgnoreCase(method) && PATH_MATCHER.match(patternUri, uri)) {
                return true;
            }
        }
        return false;
    }

    private void forbidden(HttpServletResponse response) throws Exception {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(Result.failure(StatusEnum.NO_PERMISSION).asJsonString());
    }
}
