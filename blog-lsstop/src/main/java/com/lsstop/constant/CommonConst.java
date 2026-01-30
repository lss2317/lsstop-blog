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
     * 点赞状态：取消
     */
    public static final Integer LIKE_STATUS_CANCEL = 0;

    /**
     * 点赞状态：点赞
     */
    public static final Integer LIKE_STATUS_LIKED = 1;

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
     * 无效的评论目标类型
     */
    public static final String INVALID_COMMENT_TYPE = "无效的评论目标类型";

    /**
     * 无效的点赞类型
     */
    public static final String INVALID_LIKE_TYPE = "无效的点赞类型";

    /**
     * 敏感词检测异常消息
     */
    public static final String SENSITIVE_WORD_DETECTED = "内容包含敏感词，请修改后重试";
}
