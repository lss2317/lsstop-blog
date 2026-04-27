package com.lsstop.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 新增评论返回VO
 *
 * @author lishusheng
 * @date 2026/02/02
 */
@Data
public class AddCommentVO {

    /**
     * 评论id
     */
    private Integer id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 评论内容
     */
    private String content;

    /**
     * IP归属地
     */
    private String ipRegion;

    /**
     * 审核状态（0-正常 1-待审核）
     */
    private Integer review;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
