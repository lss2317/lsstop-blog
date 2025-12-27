package com.lsstop.domain.vo;

import lombok.Data;

/**
 * 友链VO
 *
 * @author lishusheng
 * @date 2025/12/27
 */
@Data
public class FriendLinkVo {

    /**
     * 链接名
     */
    private String linkName;

    /**
     * 链接头像
     */
    private String linkAvatar;

    /**
     * 链接地址
     */
    private String linkAddress;

    /**
     * 介绍
     */
    private String linkIntro;

}
