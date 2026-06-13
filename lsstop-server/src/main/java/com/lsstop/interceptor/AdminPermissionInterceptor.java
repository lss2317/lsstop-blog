package com.lsstop.interceptor;

import com.lsstop.common.Result;
import com.lsstop.enums.StatusEnum;
import com.lsstop.service.ApiPermissionService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

/**
 * 后台接口权限拦截器
 * <p>责任：基于 blog_api_permission 三表体系进行接口级权限校验
 * <p>执行顺序在 AdminAuthInterceptor 之后，依赖其设置的 userId 属性
 * <p>校验策略：
 * <ul>
 *   <li>当前请求未在接口权限表中注册 → 放行（未纳入管控）</li>
 *   <li>当前请求已注册且用户拥有匹配权限 → 放行</li>
 *   <li>当前请求已注册但用户无匹配权限 → 403</li>
 * </ul>
 *
 * @author lishusheng
 * @date 2026/05/05
 */
@Slf4j
@Component
public class AdminPermissionInterceptor implements HandlerInterceptor {

    @Resource
    private ApiPermissionService apiPermissionService;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

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

        String requestMethod = request.getMethod().toUpperCase();
        String requestUri = request.getRequestURI();

        // 1. 获取全局已注册权限，判断当前 URL 是否受控
        Set<String> registeredPermissions = apiPermissionService.getAllRegisteredApiPermissions();
        if (!hasMatch(requestMethod, requestUri, registeredPermissions)) {
            // URL 未纳入权限管控，放行
            return true;
        }

        // 2. URL 受控，检查用户是否拥有匹配权限
        Set<String> userPermissions = apiPermissionService.getUserEffectiveApiPermissions(userId);
        if (hasMatch(requestMethod, requestUri, userPermissions)) {
            return true;
        }

        // 无权限
        log.warn("接口权限校验未通过: userId={}, {} {}", userId, requestMethod, requestUri);
        forbidden(response);
        return false;
    }

    /**
     * 判断权限集合中是否存在匹配当前请求的模式
     */
    private boolean hasMatch(String method, String uri, Set<String> permissions) {
        for (String pattern : permissions) {
            if (matchPermission(method, uri, pattern)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 匹配单个权限模式
     * <p>格式：METHOD:/uri/pattern，如 GET:/admin/article/list、DELETE:/admin/article/*
     *
     * @param method  当前请求方法
     * @param uri     当前请求 URI
     * @param pattern 权限模式（METHOD:/path/pattern）
     * @return 是否匹配
     */
    private boolean matchPermission(String method, String uri, String pattern) {
        int colonIndex = pattern.indexOf(':');
        if (colonIndex <= 0) {
            return false;
        }
        String patternMethod = pattern.substring(0, colonIndex);
        String patternPath = pattern.substring(colonIndex + 1);

        // 方法必须一致
        if (!method.equalsIgnoreCase(patternMethod)) {
            return false;
        }
        // 使用 AntPathMatcher 进行路径匹配
        return pathMatcher.match(patternPath, uri);
    }

    private void forbidden(HttpServletResponse response) throws Exception {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(Result.failure(StatusEnum.NO_PERMISSION).asJsonString());
    }
}
