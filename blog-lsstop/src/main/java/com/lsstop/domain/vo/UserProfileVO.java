package com.lsstop.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户主页详情VO
 *
 * @author lishusheng
 * @date 2026/03/10
 */
@Data
public class UserProfileVO {

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
     * IP所属地
     */
    private String ipRegion;

    /**
     * 邮箱（脱敏）
     */
    private String email;

    /**
     * QQ绑定状态
     */
    private Boolean qqBound;

    /**
     * 微博绑定状态
     */
    private Boolean weiboBound;

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
