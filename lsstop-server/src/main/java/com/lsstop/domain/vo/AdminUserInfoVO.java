package com.lsstop.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 后台用户信息VO
 *
 * @author lishusheng
 * @date 2026/05/03
 */
@Data
public class AdminUserInfoVO {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 个人简介
     */
    private String intro;

    /**
     * 个人网站
     */
    private String website;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 是否绑定QQ
     */
    private Boolean qqBound;

    /**
     * 是否绑定微博
     */
    private Boolean weiboBound;

    /**
     * 角色列表
     */
    private List<RoleBrief> roles;
}
