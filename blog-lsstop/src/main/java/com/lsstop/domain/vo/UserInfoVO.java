package com.lsstop.domain.vo;

import lombok.Data;

/**
 * 用户信息VO
 *
 * @author lishusheng
 * @date 2026/01/03
 */
@Data
public class UserInfoVO {

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
}
