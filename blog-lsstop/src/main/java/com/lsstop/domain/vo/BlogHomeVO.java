package com.lsstop.domain.vo;

import lombok.Data;

/**
 * 首页博客VO
 *
 * @author lishusheng
 * @date 2026/02/17
 */
@Data
public class BlogHomeVO {

    /**
     * 网站头像
     */
    private String siteAvatar;

    /**
     * 网站名称
     */
    private String siteName;

    /**
     * 网站介绍
     */
    private String siteIntro;

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
     * 文章总数
     */
    private Integer articleCount;

    /**
     * 分类总数
     */
    private Integer categoryCount;

    /**
     * 标签总数
     */
    private Integer tagCount;

    /**
     * 网站访问量
     */
    private Integer viewCount;

}
