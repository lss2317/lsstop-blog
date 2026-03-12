package com.lsstop.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户公开主页VO（用于查看他人主页）
 *
 * @author lishusheng
 * @date 2026/03/12
 */
@Data
public class UserPublicProfileVO {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 个人网站
     */
    private String website;

    /**
     * 个人简介
     */
    private String intro;

    /**
     * 评论数量
     */
    private Integer commentCount;

    /**
     * 获赞数量
     */
    private Integer likeCount;

    /**
     * 注册时间
     */
    private LocalDateTime createTime;
}
