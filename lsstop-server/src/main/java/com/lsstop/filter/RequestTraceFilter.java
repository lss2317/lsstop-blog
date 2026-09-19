package com.lsstop.filter;

import com.lsstop.constant.NotificationConst;
import com.lsstop.constant.RequestTraceConst;
import com.lsstop.service.NotificationService;
import com.lsstop.utils.IpUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
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
@RequiredArgsConstructor
public class RequestTraceFilter extends OncePerRequestFilter {

    private final NotificationService notificationService;

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
        } catch (ServletException | IOException | RuntimeException e) {
            int errorStatus = Math.max(response.getStatus(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            if (notificationService.recordHttpException(request, errorStatus, e)) {
                request.setAttribute(RequestTraceConst.NOTIFICATION_RECORDED, Boolean.TRUE);
            }
            throw e;
        } finally {
            try {
                if (shouldWriteAccessLog(request)) {
                    long costTime = System.currentTimeMillis() - startTime;
                    writeAccessLog(request, response, costTime);
                    if (costTime >= NotificationConst.SLOW_REQUEST_THRESHOLD_MS) {
                        notificationService.recordSlowRequest(request, response, costTime);
                    }
                    if (response.getStatus() >= HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                            && !Boolean.TRUE.equals(request.getAttribute(RequestTraceConst.NOTIFICATION_RECORDED))) {
                        if (notificationService.recordHttpStatusError(request, response, costTime)) {
                            request.setAttribute(RequestTraceConst.NOTIFICATION_RECORDED, Boolean.TRUE);
                        }
                    }
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
        } else if (costTime >= NotificationConst.SLOW_REQUEST_THRESHOLD_MS) {
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
        return truncate(result, NotificationConst.MAX_PARAMS_LENGTH);
    }

    private String getSafeValue(String fieldName, String[] values) {
        boolean sensitive = NotificationConst.SENSITIVE_FIELDS.contains(fieldName.toLowerCase(Locale.ROOT));
        if (sensitive) {
            return NotificationConst.MASKED_VALUE;
        }
        String value = values == null ? "" : Arrays.toString(values);
        return truncate(value, NotificationConst.MAX_PARAM_VALUE_LENGTH);
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
