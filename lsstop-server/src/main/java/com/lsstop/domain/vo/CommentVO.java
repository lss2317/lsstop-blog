package com.lsstop.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论VO
 *
 * @author lishusheng
 * @date 2026/02/01
 */
@Data
public class CommentVO {

    /**
     * 评论id
     */
    private Integer id;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户id
     */
    private String userId;

    /**
     * IP属地
     */
    private String ipRegion;

    /**
     * 评论时间
     */
    private LocalDateTime createTime;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 回复数
     */
    private Integer replyCount;
}
