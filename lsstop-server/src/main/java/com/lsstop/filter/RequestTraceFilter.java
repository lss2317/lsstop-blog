package com.lsstop.filter;

import com.lsstop.constant.RequestTraceConst;
import com.lsstop.utils.IpUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * HTTP请求链路追踪与访问日志过滤器
 *
 * @author lishusheng
 * @date 2026/09/06
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestTraceFilter extends OncePerRequestFilter {

    private static final long SLOW_REQUEST_THRESHOLD_MS = 1000L;
    private static final int MAX_PARAM_VALUE_LENGTH = 200;
    private static final int MAX_PARAMS_LENGTH = 2000;
    private static final String MASKED_VALUE = "******";
    private static final Set<String> SENSITIVE_FIELDS = Set.of(
            "password", "oldPassword", "newPassword", "confirmPassword",
            "token", "accessToken", "refreshToken", "authorization",
            "cookie", "code", "credential", "secret"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString().replace("-", "");
        long startTime = System.currentTimeMillis();

        request.setAttribute(RequestTraceConst.REQUEST_ID, requestId);
        response.setHeader(RequestTraceConst.REQUEST_ID_HEADER, requestId);
        MDC.put(RequestTraceConst.REQUEST_ID, requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            try {
                if (shouldWriteAccessLog(request)) {
                    writeAccessLog(request, response, System.currentTimeMillis() - startTime);
                }
            } finally {
                MDC.remove(RequestTraceConst.REQUEST_ID);
            }
        }
    }

    /**
     * 打印单条请求摘要，不记录请求体、响应体和敏感参数
     */
    private void writeAccessLog(HttpServletRequest request, HttpServletResponse response, long costTime) {
        String method = request.getMethod();
        String requestUrl = request.getRequestURI();
        String params = getSafeParams(request);
        String userId = (String) request.getAttribute("userId");
        String ipAddress = IpUtils.getIpAddress(request);
        int status = response.getStatus();

        String message = "请求完成 method={}, url={}, params={}, status={}, costTime={}ms, userId={}, ip={}";
        Object[] args = {method, requestUrl, params, status, costTime, userId, ipAddress};

        if (status >= HttpServletResponse.SC_INTERNAL_SERVER_ERROR) {
            log.error(message, args);
        } else if (costTime >= SLOW_REQUEST_THRESHOLD_MS) {
            log.warn(message, args);
        } else {
            log.info(message, args);
        }
    }

    /**
     * 获取经过脱敏和截断的请求参数
     */
    private String getSafeParams(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap.isEmpty()) {
            return "{}";
        }

        String result = parameterMap.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + getSafeValue(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(", ", "{", "}"));
        return truncate(result, MAX_PARAMS_LENGTH);
    }

    private String getSafeValue(String fieldName, String[] values) {
        boolean sensitive = SENSITIVE_FIELDS.stream()
                .anyMatch(field -> field.equalsIgnoreCase(fieldName));
        if (sensitive) {
            return MASKED_VALUE;
        }
        String value = values == null ? "" : Arrays.toString(values);
        return truncate(value, MAX_PARAM_VALUE_LENGTH);
    }

    private String truncate(String value, int maxLength) {
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength) + "...";
    }

    /**
     * 预检、WebSocket及静态资源仍会获得requestId，但不打印高频访问日志
     */
    private boolean shouldWriteAccessLog(HttpServletRequest request) {
        String method = request.getMethod().toUpperCase(Locale.ROOT);
        String uri = request.getRequestURI();
        return !"OPTIONS".equals(method)
                && !uri.startsWith("/ws/")
                && !uri.startsWith("/favicon")
                && !uri.startsWith("/assets/");
    }
}
