package com.lsstop.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户最近评论VO
 *
 * @author lishusheng
 * @date 2026/03/19
 */
@Data
public class UserRecentCommentVO {

    /**
     * 评论id
     */
    private Integer id;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 目标类型 1文章 2友链 3说说
     */
    private Integer targetType;

    /**
     * 目标id
     */
    private Integer targetId;

    /**
     * 目标标题（文章标题/说说内容摘要，友链时为空）
     */
    private String targetTitle;

    /**
     * 评论时间
     */
    private LocalDateTime createTime;
}
