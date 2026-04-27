package com.lsstop.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 网站配置VO
 *
 * @author lishusheng
 * @date 2025/12/25
 */
@Data
public class WebsiteConfigVO {

    /**
     * 网站头像
     */
    private String siteAvatar;

    /**
     * 网站名称
     */
    private String siteName;

    /**
     * 网站作者
     */
    private String siteAuthor;

    /**
     * 网站介绍
     */
    private String siteIntro;

    /**
     * 关于我
     */
    private String about;

    /**
     * 网站创建时间
     */
    private LocalDateTime siteStartTime;

    /**
     * QQ链接
     */
    private String qqUrl;

    /**
     * GitHub链接
     */
    private String githubUrl;

    /**
     * Gitee链接
     */
    private String giteeUrl;

    /**
     * 用户默认头像
     */
    private String defaultUserAvatar;

    /**
     * websocket地址
     */
    private String websocketUrl;

}
