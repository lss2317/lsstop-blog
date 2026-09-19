package com.lsstop.service.impl;

import com.alibaba.fastjson2.JSON;
import com.lsstop.constant.NotificationConst;
import com.lsstop.constant.RequestTraceConst;
import com.lsstop.domain.dto.NotificationQueryDTO;
import com.lsstop.domain.entity.NotificationEntity;
import com.lsstop.domain.vo.NotificationDetailVO;
import com.lsstop.domain.vo.NotificationListVO;
import com.lsstop.enums.NotificationCategoryEnum;
import com.lsstop.enums.NotificationEventTypeEnum;
import com.lsstop.enums.NotificationLevelEnum;
import com.lsstop.enums.NotificationSourceTypeEnum;
import com.lsstop.enums.StatusEnum;
import com.lsstop.exception.BusinessException;
import com.lsstop.mapper.NotificationMapper;
import com.lsstop.service.NotificationService;
import com.lsstop.utils.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerMapping;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * 系统通知及异常告警记录服务实现。
 *
 * <p>业务线程只负责生成完整通知快照并投递到专用线程池，数据库操作由通知线程异步执行。
 * 请求对象中的参数和属性必须在投递前读取，因为异步线程执行时原请求可能已经结束。
 * 通知记录链路的异常均在本服务内消化，避免告警构建、线程池拒绝或数据库故障覆盖原始业务异常；
 * 后台查询和阅读状态操作则按普通业务接口规则返回错误。</p>
 *
 * @author lishusheng
 * @date 2026/09/19
 */
@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    /** 负责在独立事务中执行通知 upsert */
    private final NotificationPersistenceService persistenceService;

    /** 通知异步入库专用线程池，不与主业务线程池共用 */
    private final TaskExecutor notificationTaskExecutor;

    /** 通知查询及阅读状态数据访问组件 */
    private final NotificationMapper notificationMapper;

    /**
     * 注入通知持久化服务和专用异步执行器。
     *
     * @param persistenceService 通知独立事务持久化服务
     * @param notificationTaskExecutor 通知异步入库专用执行器
     * @param notificationMapper 通知查询及状态更新数据访问组件
     */
    public NotificationServiceImpl(
            NotificationPersistenceService persistenceService,
            @Qualifier(NotificationConst.TASK_EXECUTOR_BEAN_NAME) TaskExecutor notificationTaskExecutor,
            NotificationMapper notificationMapper) {
        this.persistenceService = persistenceService;
        this.notificationTaskExecutor = notificationTaskExecutor;
        this.notificationMapper = notificationMapper;
    }

    @Override
    public boolean recordHttpException(HttpServletRequest request, int status, Throwable throwable) {
        try {
            // 使用最底层原因生成摘要和聚合特征，避免不同包装异常把同一故障拆成多条通知。
            Throwable rootCause = getRootCause(throwable);
            String method = request.getMethod();
            String route = getRoute(request);
            String requestId = getRequestId(request);

            // 在请求线程中完成 Servlet 请求信息快照，异步线程不再访问 request 对象。
            Map<String, Object> extra = buildHttpExtra(request, status);
            extra.put("exceptionClass", rootCause.getClass().getName());
            extra.put("exceptionMessage", sanitizeText(rootCause.getMessage()));
            extra.put("stackTrace", getStackTrace(rootCause));

            return persist(
                    NotificationCategoryEnum.EXCEPTION_ALERT,
                    NotificationEventTypeEnum.SYSTEM_EXCEPTION,
                    NotificationLevelEnum.ERROR,
                    "接口异常：" + method + " " + route,
                    rootCause.getClass().getSimpleName() + "：" + defaultMessage(rootCause.getMessage()),
                    NotificationSourceTypeEnum.HTTP_REQUEST,
                    requestId,
                    extra,
                    method + "|" + route + "|" + rootCause.getClass().getName() + "|" + firstStackLocation(rootCause)
            );
        } catch (Exception recordError) {
            return handleRecordFailure(recordError);
        }
    }

    @Override
    public boolean recordHttpStatusError(HttpServletRequest request, HttpServletResponse response, long costTime) {
        try {
            // 此入口没有异常对象，以请求方法、路由和状态码作为稳定的聚合依据。
            String method = request.getMethod();
            String route = getRoute(request);
            int status = response.getStatus();
            Map<String, Object> extra = buildHttpExtra(request, status);
            extra.put("costTime", costTime);

            return persist(
                    NotificationCategoryEnum.EXCEPTION_ALERT,
                    NotificationEventTypeEnum.SYSTEM_EXCEPTION,
                    NotificationLevelEnum.ERROR,
                    "接口返回异常状态：" + method + " " + route,
                    "HTTP状态码 " + status + "，请求耗时 " + costTime + "ms",
                    NotificationSourceTypeEnum.HTTP_REQUEST,
                    getRequestId(request),
                    extra,
                    method + "|" + route + "|HTTP_STATUS_" + status
            );
        } catch (Exception recordError) {
            return handleRecordFailure(recordError);
        }
    }

    @Override
    public boolean recordSlowRequest(HttpServletRequest request, HttpServletResponse response, long costTime) {
        try {
            // 同一接口的慢请求在固定窗口内聚合，不把每次耗时放入去重材料。
            String method = request.getMethod();
            String route = getRoute(request);
            Map<String, Object> extra = buildHttpExtra(request, response.getStatus());
            extra.put("costTime", costTime);

            return persist(
                    NotificationCategoryEnum.EXCEPTION_ALERT,
                    NotificationEventTypeEnum.SLOW_API,
                    NotificationLevelEnum.WARNING,
                    "接口请求过慢：" + method + " " + route,
                    "请求耗时 " + costTime + "ms，HTTP状态码 " + response.getStatus(),
                    NotificationSourceTypeEnum.HTTP_REQUEST,
                    getRequestId(request),
                    extra,
                    method + "|" + route
            );
        } catch (Exception recordError) {
            return handleRecordFailure(recordError);
        }
    }

    @Override
    public boolean recordFailure(NotificationEventTypeEnum eventType,
                                 NotificationSourceTypeEnum sourceType,
                                 NotificationLevelEnum level,
                                 String title,
                                 String sourceId,
                                 Throwable throwable,
                                 Map<String, ?> details) {
        try {
            // 非 HTTP 场景统一抽取根因，并保留调用方提供的业务定位信息。
            Throwable rootCause = getRootCause(throwable);
            Map<String, Object> extra = new LinkedHashMap<>();
            if (details != null) {
                extra.putAll(details);
            }

            // 若当前故障由 HTTP 请求触发，则补充 MDC 中的请求编号，串联请求日志和通知。
            String requestId = MDC.get(RequestTraceConst.REQUEST_ID);
            if (requestId != null && !requestId.isBlank()) {
                extra.putIfAbsent(RequestTraceConst.REQUEST_ID, requestId);
            }
            extra.put("exceptionClass", rootCause.getClass().getName());
            extra.put("exceptionMessage", sanitizeText(rootCause.getMessage()));
            extra.put("stackTrace", getStackTrace(rootCause));

            // 调用方没有明确来源编号时，使用 requestId 作为回溯入口。
            String actualSourceId = sourceId;
            if ((actualSourceId == null || actualSourceId.isBlank()) && requestId != null) {
                actualSourceId = requestId;
            }

            return persist(
                    categoryOf(eventType),
                    eventType,
                    level,
                    title,
                    rootCause.getClass().getSimpleName() + "：" + defaultMessage(rootCause.getMessage()),
                    sourceType,
                    actualSourceId,
                    extra,
                    sourceType.getType() + "|" + title + "|" + rootCause.getClass().getName()
                            + "|" + firstStackLocation(rootCause)
            );
        } catch (Exception recordError) {
            return handleRecordFailure(recordError);
        }
    }

    /**
     * 分页读取通知摘要；列表和顶部未读弹层使用相同筛选逻辑，保证数量与记录口径一致。
     */
    @Override
    public List<NotificationListVO> listNotifications(NotificationQueryDTO query) {
        return notificationMapper.selectList(query);
    }

    /**
     * 统计通知总数；右上角传未读状态时，该结果直接作为通知角标数量。
     */
    @Override
    public Integer countNotifications(NotificationQueryDTO query) {
        return notificationMapper.countTotal(query);
    }

    /**
     * 获取包含已脱敏扩展数据的通知详情。
     */
    @Override
    public NotificationDetailVO getNotificationDetail(String notificationNo) {
        NotificationDetailVO detail = notificationMapper.selectDetailByNotificationNo(notificationNo);
        if (detail == null) {
            throw new BusinessException(StatusEnum.NOT_FOUND, NotificationConst.NOTIFICATION_NOT_FOUND);
        }
        return detail;
    }

    /**
     * 标记单条通知为已读。先校验通知存在，使不存在和重复标记两种情况具有明确语义。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(String notificationNo) {
        getNotificationDetail(notificationNo);
        notificationMapper.markAsRead(notificationNo);
    }

    /**
     * 一次性更新当前全部未读通知；新发生的通知不受本次更新影响。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllAsRead() {
        notificationMapper.markAllAsRead();
    }

    /**
     * 构建不可变的通知快照并提交到异步线程池。
     *
     * <p>去重键由事件类型、业务故障特征和当前聚合窗口共同计算：同一窗口内的同类故障命中
     * 数据库唯一索引并累加次数，进入下一窗口后则生成新通知。此方法返回成功仅表示任务已被
     * 线程池接收，并不表示数据库已经写入完成。</p>
     */
    private boolean persist(NotificationCategoryEnum category,
                            NotificationEventTypeEnum eventType,
                            NotificationLevelEnum level,
                            String title,
                            String content,
                            NotificationSourceTypeEnum sourceType,
                            String sourceId,
                            Map<String, Object> extra,
                            String dedupMaterial) {
        LocalDateTime now = LocalDateTime.now();

        // 将时间按固定秒数切片，使重复事件只在当前时间窗口内合并。
        long window = now.toEpochSecond(ZoneOffset.ofHours(8)) / NotificationConst.DEDUP_WINDOW_SECONDS;

        // 所有长度限制和敏感信息处理都在业务线程完成，异步线程只接收与持久化实体快照。
        NotificationEntity notification = NotificationEntity.builder()
                .notificationNo(UUID.randomUUID().toString().replace("-", ""))
                .category(category.getCode())
                .eventType(eventType.getType())
                .level(level.getCode())
                .title(truncate(sanitizeText(title), NotificationConst.MAX_TITLE_LENGTH))
                .content(truncate(sanitizeText(content), NotificationConst.MAX_CONTENT_LENGTH))
                .sourceType(sourceType.getType())
                .sourceId(truncate(sourceId, NotificationConst.MAX_SOURCE_ID_LENGTH))
                .extra(JSON.toJSONString(extra))
                .dedupKey(sha256(eventType.getType() + "|" + dedupMaterial + "|" + window))
                .occurrenceCount(1)
                .firstOccurredTime(now)
                .lastOccurredTime(now)
                .build();

        // AbortPolicy 抛出的拒绝异常由上层入口捕获并降级记录，不会回退到业务线程执行数据库操作。
        notificationTaskExecutor.execute(() -> persistSafely(notification));
        return true;
    }

    /**
     * 在通知专用线程中执行独立事务写入。
     *
     * <p>数据库不可用、唯一键之外的约束错误等异常只记录普通日志，不继续向线程池传播，
     * 防止通知系统的故障干扰业务流程。</p>
     *
     * @param notification 待持久化的完整通知快照
     */
    private void persistSafely(NotificationEntity notification) {
        try {
            persistenceService.upsert(notification);
        } catch (Exception persistenceError) {
            log.error("系统通知异步入库失败, notificationNo={}, eventType={}",
                    notification.getNotificationNo(), notification.getEventType(), persistenceError);
        }
    }

    /**
     * 提取 HTTP 请求的诊断快照。
     *
     * <p>请求参数会在此处完成敏感字段遮蔽和长度限制，禁止把原始 request 对象交给异步线程。</p>
     */
    private Map<String, Object> buildHttpExtra(HttpServletRequest request, int status) {
        Map<String, Object> extra = new LinkedHashMap<>();
        extra.put(RequestTraceConst.REQUEST_ID, getRequestId(request));
        extra.put("method", request.getMethod());
        extra.put("uri", request.getRequestURI());
        extra.put("route", getRoute(request));
        extra.put("params", getSafeParams(request));
        extra.put("status", status);
        extra.put("userId", request.getAttribute("userId"));
        extra.put("ip", IpUtils.getIpAddress(request));
        return extra;
    }

    /**
     * 将请求参数转换为可控长度的文本，并对密码、令牌等敏感字段进行遮蔽。
     */
    private String getSafeParams(HttpServletRequest request) {
        if (request.getParameterMap().isEmpty()) {
            return "{}";
        }
        StringBuilder result = new StringBuilder("{");
        request.getParameterMap().forEach((key, values) -> {
            if (result.length() > 1) {
                result.append(", ");
            }
            result.append(key).append('=').append(getSafeValue(key, values));
        });
        result.append('}');
        return truncate(result.toString(), NotificationConst.MAX_PARAMS_LENGTH);
    }

    /**
     * 获取单个请求参数的安全展示值；敏感字段不读取实际内容。
     */
    private String getSafeValue(String fieldName, String[] values) {
        if (NotificationConst.SENSITIVE_FIELDS.contains(fieldName.toLowerCase(Locale.ROOT))) {
            return NotificationConst.MASKED_VALUE;
        }
        String value = values == null ? "" : Arrays.toString(values);
        return truncate(sanitizeText(value), NotificationConst.MAX_PARAM_VALUE_LENGTH);
    }

    /**
     * 优先返回 Spring 匹配到的路由模板，使不同路径参数的请求能够按同一接口聚合。
     */
    private String getRoute(HttpServletRequest request) {
        Object route = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        return route == null ? request.getRequestURI() : route.toString();
    }

    private String getRequestId(HttpServletRequest request) {
        Object requestId = request.getAttribute(RequestTraceConst.REQUEST_ID);
        return requestId == null ? MDC.get(RequestTraceConst.REQUEST_ID) : requestId.toString();
    }

    /**
     * 根据事件语义确定通知分类；未单独定义的故障统一归入异常告警。
     */
    private NotificationCategoryEnum categoryOf(NotificationEventTypeEnum eventType) {
        if (eventType == NotificationEventTypeEnum.LOGIN_RISK) {
            return NotificationCategoryEnum.SECURITY_ALERT;
        }
        if (eventType == NotificationEventTypeEnum.SYSTEM_NOTICE) {
            return NotificationCategoryEnum.SYSTEM_NOTICE;
        }
        return NotificationCategoryEnum.EXCEPTION_ALERT;
    }

    /**
     * 沿异常 cause 链获取最底层原因，空异常统一转换为可记录的未知异常。
     */
    private Throwable getRootCause(Throwable throwable) {
        Throwable result = throwable == null ? new IllegalStateException("未知系统异常") : throwable;
        while (result.getCause() != null && result.getCause() != result) {
            result = result.getCause();
        }
        return result;
    }

    /**
     * 提取首个项目代码栈位置参与去重，避免仅凭异常类型合并不同代码位置的故障。
     */
    private String firstStackLocation(Throwable throwable) {
        for (StackTraceElement element : throwable.getStackTrace()) {
            if (element.getClassName().startsWith("com.lsstop.")) {
                return element.getClassName() + ":" + element.getMethodName() + ":" + element.getLineNumber();
            }
        }
        return throwable.getStackTrace().length == 0 ? "unknown" : throwable.getStackTrace()[0].toString();
    }

    private String getStackTrace(Throwable throwable) {
        StringWriter writer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(writer));
        return truncate(sanitizeText(writer.toString()), NotificationConst.MAX_STACK_TRACE_LENGTH);
    }

    private String defaultMessage(String message) {
        String sanitized = sanitizeText(message);
        return sanitized == null || sanitized.isBlank() ? "无异常描述" : sanitized;
    }

    private String sanitizeText(String value) {
        if (value == null) {
            return null;
        }
        return NotificationConst.SENSITIVE_TEXT_PATTERN.matcher(value)
                .replaceAll("$1$2" + NotificationConst.MASKED_VALUE);
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength - 3) + "...";
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("当前JVM不支持SHA-256", e);
        }
    }

    /**
     * 统一处理通知快照构建或异步任务投递失败，保证记录链路永不向业务调用方抛异常。
     */
    private boolean handleRecordFailure(Exception recordError) {
        log.error("系统通知构建或投递失败", recordError);
        return false;
    }
}
