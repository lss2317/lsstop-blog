package com.lsstop.domain.vo;

import lombok.Data;

/**
 * 登录vo
 *
 * @author lishusheng
 * @date 2026/01/03
 */
@Data
public class LoginVO {

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
     * 登录方式：1邮箱密码 2QQ 3微博
     */
    private Integer loginType;

    /**
     * accessToken
     */
    private String accessToken;

    /**
     * refreshToken
     */
    private String refreshToken;
}
