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
     * 目标ID不能为空
     */
    public static final String TARGET_ID_REQUIRED = "目标ID不能为空";

    /**
     * 友链评论默认目标ID
     */
    public static final Integer FRIEND_LINK_DEFAULT_TARGET_ID = 0;

    /**
     * 无效的点赞类型
     */
    public static final String INVALID_LIKE_TYPE = "无效的点赞类型";

    /**
     * 敏感词检测异常消息
     */
    public static final String SENSITIVE_WORD_DETECTED = "内容包含敏感词，请修改后重试";

    /**
     * 无效的排序类型
     */
    public static final String INVALID_SORT_TYPE = "无效的排序类型";

    /**
     * 分页参数错误
     */
    public static final String INVALID_PAGE_PARAM = "分页参数错误";

    /**
     * 排序类型：最热
     */
    public static final String SORT_TYPE_HOT = "hot";

    /**
     * 排序类型：最新
     */
    public static final String SORT_TYPE_NEW = "new";

    /**
     * 默认每页数量
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 子评论默认限制数量
     */
    public static final int DEFAULT_CHILD_COMMENT_LIMIT = 5;

    /**
     * 父评论ID不能为空
     */
    public static final String PARENT_ID_REQUIRED = "父评论ID不能为空";
}
