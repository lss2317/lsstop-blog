package com.lsstop.constant;

/**
 * 评论模块常量
 *
 * @author lishusheng
 * @date 2026/02/08
 */
public class CommentConst {

    /**
     * 无效的评论目标类型
     */
    public static final String INVALID_COMMENT_TYPE = "无效的评论目标类型";

    /**
     * 友链评论默认目标ID
     */
    public static final Integer FRIEND_LINK_DEFAULT_TARGET_ID = 0;

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

    /**
     * 评论不存在
     */
    public static final String COMMENT_NOT_FOUND = "评论不存在";

    /**
     * 无权限删除该评论
     */
    public static final String NO_PERMISSION_DELETE_COMMENT = "无权限删除该评论";

    /**
     * 评论ID不能为空
     */
    public static final String COMMENT_ID_REQUIRED = "评论ID不能为空";

    /**
     * 用户最近评论限制数量
     */
    public static final int RECENT_COMMENT_LIMIT = 10;
}
