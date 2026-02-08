package com.lsstop.constant;

/**
 * 通用常量
 *
 * @author lishusheng
 * @date 2026/01/27
 */
public class CommonConst {

    /**
     * 禁用
     */
    public static final Integer DISABLED = 0;

    /**
     * 启用
     */
    public static final Integer ENABLED = 1;

    /**
     * 用户状态：禁用
     */
    public static final Integer USER_STATUS_DISABLED = 0;

    /**
     * 用户状态：正常
     */
    public static final Integer USER_STATUS_NORMAL = 1;

    /**
     * 未知
     */
    public static final String UNKNOWN = "IP未知";

    /**
     * 审核状态：正常
     */
    public static final Integer REVIEW_NORMAL = 0;

    /**
     * 审核状态：待审核
     */
    public static final Integer REVIEW_PENDING = 1;

    /**
     * 目标ID不能为空
     */
    public static final String TARGET_ID_REQUIRED = "目标ID不能为空";

    /**
     * 用户ID不能为空
     */
    public static final String USER_ID_REQUIRED = "用户ID不能为空";

    /**
     * 敏感词检测异常消息
     */
    public static final String SENSITIVE_WORD_DETECTED = "内容包含敏感词，请修改后重试";
}
