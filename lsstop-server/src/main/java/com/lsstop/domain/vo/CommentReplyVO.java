package com.lsstop.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论回复VO
 *
 * @author lishusheng
 * @date 2026/02/01
 */
@Data
public class CommentReplyVO {

    /**
     * 回复id
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
     * 回复时间
     */
    private LocalDateTime createTime;

    /**
     * 回复内容
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 被回复者id
     */
    private String replyUserId;

    /**
     * 被回复者昵称
     */
    private String replyNickname;

    /**
     * 父评论id（用于组装回复列表）
     */
    private Integer parentId;
}
