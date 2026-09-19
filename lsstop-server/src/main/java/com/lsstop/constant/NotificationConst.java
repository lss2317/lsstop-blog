package com.lsstop.constant;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * 系统通知及异常告警常量
 *
 * @author lishusheng
 * @date 2026/09/19
 */
public final class NotificationConst {

    /**
     * 通知异步入库线程池Bean名称
     */
    public static final String TASK_EXECUTOR_BEAN_NAME = "notificationTaskExecutor";

    /**
     * 通知列表单页允许查询的最大记录数
     */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * 未读状态查询值
     */
    public static final int READ_STATUS_UNREAD = 0;

    /**
     * 已读状态查询值
     */
    public static final int READ_STATUS_READ = 1;

    /**
     * 通知不存在提示
     */
    public static final String NOTIFICATION_NOT_FOUND = "通知不存在";

    /**
     * 通知列表分页参数错误提示
     */
    public static final String INVALID_PAGE_PARAM = "分页参数错误";

    /**
     * 通知阅读状态参数错误提示
     */
    public static final String INVALID_READ_STATUS = "通知阅读状态参数错误";

    /**
     * 通知分类参数错误提示
     */
    public static final String INVALID_CATEGORY = "通知分类参数错误";

    /**
     * 通知级别参数错误提示
     */
    public static final String INVALID_LEVEL = "通知级别参数错误";

    /**
     * 通知查询时间范围错误提示
     */
    public static final String INVALID_TIME_RANGE = "开始时间不能晚于结束时间";

    /**
     * 慢请求判定阈值，单位为毫秒
     */
    public static final long SLOW_REQUEST_THRESHOLD_MS = 1000L;

    /**
     * 重复事件聚合窗口，单位为秒；相同事件在五分钟内重复发生时合并为一条通知并累加发生次数
     */
    public static final long DEDUP_WINDOW_SECONDS = 5 * 60L;

    /**
     * 通知标题最大长度，对应数据库title字段长度，超出部分会被截断
     */
    public static final int MAX_TITLE_LENGTH = 100;

    /**
     * 通知摘要最大长度，对应数据库content字段长度，避免异常信息过长导致入库失败
     */
    public static final int MAX_CONTENT_LENGTH = 500;

    /**
     * 通知来源编号最大长度，对应数据库source_id字段长度
     */
    public static final int MAX_SOURCE_ID_LENGTH = 64;

    /**
     * 单个HTTP请求参数值的最大记录长度，超出部分会被截断
     */
    public static final int MAX_PARAM_VALUE_LENGTH = 200;

    /**
     * 全部HTTP请求参数拼接后的最大记录长度，避免扩展JSON体积失控
     */
    public static final int MAX_PARAMS_LENGTH = 2000;

    /**
     * 异常堆栈最大记录长度，保留主要排查信息并限制通知数据体积
     */
    public static final int MAX_STACK_TRACE_LENGTH = 8000;

    /**
     * 敏感信息脱敏占位符
     */
    public static final String MASKED_VALUE = "******";

    /**
     * HTTP请求参数中需要脱敏的字段名，统一使用小写匹配
     */
    public static final Set<String> SENSITIVE_FIELDS = Set.of(
            "password", "oldpassword", "newpassword", "confirmpassword",
            "token", "accesstoken", "refreshtoken", "authorization",
            "cookie", "code", "credential", "secret"
    );

    /**
     * 异常消息及堆栈中的敏感键值匹配规则
     */
    public static final Pattern SENSITIVE_TEXT_PATTERN = Pattern.compile(
            "(?i)(password|oldPassword|newPassword|confirmPassword|token|accessToken|refreshToken|authorization|cookie|code|credential|secret)"
                    + "(\\s*[=:]\\s*)([^,;\\s&}]+)"
    );

    private NotificationConst() {
    }
}
