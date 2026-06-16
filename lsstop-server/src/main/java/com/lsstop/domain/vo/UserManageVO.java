package com.lsstop.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户管理列表项VO
 *
 * @author lishusheng
 * @date 2026/06/16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserManageVO {

    /**
     * 用户ID
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
     * 邮箱
     */
    private String email;

    /**
     * 个人网站
     */
    private String website;

    /**
     * 个人简介
     */
    private String intro;

    /**
     * 是否绑定QQ
     */
    private Boolean qqBound;

    /**
     * 是否绑定微博
     */
    private Boolean weiboBound;

    /**
     * 状态：0-禁用 1-正常
     */
    private Integer status;

    /**
     * 最近登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 角色列表
     */
    private List<UserManageRoleVO> roles;
}
