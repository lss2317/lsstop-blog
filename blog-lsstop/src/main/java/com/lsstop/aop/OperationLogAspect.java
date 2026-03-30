package com.lsstop.aop;

import com.alibaba.fastjson2.JSON;
import com.lsstop.annotation.OperationLog;
import com.lsstop.constant.CommonConst;
import com.lsstop.constant.OperationLogConst;
import com.lsstop.constant.RabbitMQConst;
import com.lsstop.domain.entity.OperationLogEntity;
import com.lsstop.utils.IpUtils;
import com.lsstop.utils.UserAgentUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

/**
 * 操作日志切面
 *
 * @author lishusheng
 * @date 2026/03/29
 */
@Slf4j
@Aspect
@Component
@Order(2)
@RequiredArgsConstructor
public class OperationLogAspect {

    private final RabbitTemplate rabbitTemplate;

    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperationLog operationLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        HttpServletRequest request = getRequest();

        // 执行目标方法
        Object result;
        int state = OperationLogConst.STATE_SUCCESS;
        String errorMsg = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            state = OperationLogConst.STATE_FAIL;
            errorMsg = truncate(e.getMessage(), OperationLogConst.MAX_ERROR_MSG_LENGTH);
            throw e;
        } finally {
            // 计算耗时
            long costTime = System.currentTimeMillis() - startTime;

            // 记录日志
            try {
                recordLog(joinPoint, operationLog, request, costTime, state, errorMsg);
            } catch (Exception e) {
                log.error("操作日志记录失败", e);
            }
        }

        return result;
    }

    /**
     * 记录操作日志
     */
    private void recordLog(ProceedingJoinPoint joinPoint, OperationLog operationLog,
                           HttpServletRequest request, long costTime, Integer state, String errorMsg) {
        String userId = null;
        String ipAddress = CommonConst.UNKNOWN;
        String ipRegion = CommonConst.UNKNOWN_REGION;
        String browser = CommonConst.UNKNOWN;
        String os = CommonConst.UNKNOWN;
        String requestUrl = "";

        if (request != null) {
            userId = (String) request.getAttribute("userId");
            ipAddress = IpUtils.getIpAddress(request);
            ipRegion = IpUtils.getIpLocation(ipAddress);
            browser = UserAgentUtils.getBrowser(request);
            os = UserAgentUtils.getOS(request);
            requestUrl = request.getRequestURI();
        }

        // 获取请求参数
        String requestParam = getRequestParam(joinPoint);

        // 构建日志描述
        String description = operationLog.description();
        if (description.isEmpty()) {
            description = operationLog.type().getDesc() + operationLog.module().getDesc();
        }

        OperationLogEntity logEntity = OperationLogEntity.builder()
                .userId(userId)
                .module(operationLog.module().name())
                .operationType(operationLog.type().name())
                .description(description)
                .requestUrl(requestUrl)
                .requestParam(requestParam)
                .ipAddress(ipAddress)
                .ipRegion(ipRegion)
                .browser(browser)
                .os(os)
                .costTime((int) costTime)
                .state(state)
                .errorMsg(errorMsg)
                .build();

        // 发送到MQ
        rabbitTemplate.convertAndSend(RabbitMQConst.BLOG_EXCHANGE, RabbitMQConst.OPERATION_LOG_ROUTING_KEY, logEntity);
    }

    /**
     * 获取请求参数
     */
    private String getRequestParam(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return "";
        }

        StringBuilder params = new StringBuilder();
        for (Object arg : args) {
            if (arg == null) {
                continue;
            }
            // 过滤不可序列化的参数
            if (arg instanceof HttpServletRequest
                    || arg instanceof jakarta.servlet.http.HttpServletResponse
                    || arg instanceof MultipartFile
                    || arg instanceof MultipartFile[]
                    || arg instanceof Collection && !((Collection<?>) arg).isEmpty() && ((Collection<?>) arg).iterator().next() instanceof MultipartFile
                    || arg instanceof BindingResult
                    || arg instanceof InputStream
                    || arg instanceof OutputStream) {
                continue;
            }
            try {
                String json = JSON.toJSONString(arg);
                // 脱敏处理
                for (String field : OperationLogConst.SENSITIVE_FIELDS) {
                    json = json.replaceAll("\"" + field + "\":\"[^\"]*\"", "\"" + field + "\":\"******\"");
                }
                params.append(json).append(" ");
            } catch (Exception e) {
                params.append(arg.toString()).append(" ");
            }
        }
        String result = params.toString().trim();
        return truncate(result, OperationLogConst.MAX_PARAM_LENGTH);
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    /**
     * 截断字符串
     */
    private String truncate(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength) + "...";
    }
}
