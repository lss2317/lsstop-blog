package com.lsstop.constant;

/**
 * 文章相关常量
 *
 * @author lishusheng
 * @date 2026/01/24
 */
public class ArticleConst {

    /**
     * 最新文章显示数量
     */
    public static final int NEWEST_ARTICLE_LIMIT = 5;

    /**
     * 推荐文章显示数量
     */
    public static final int RECOMMEND_ARTICLE_LIMIT = 6;

    /**
     * 最新文章缓存过期时间（秒）
     */
    public static final long NEWEST_ARTICLE_CACHE_EXPIRE = 5 * 60;

    /**
     * 浏览记录过期时间（秒）- 同一IP 1小时内不重复计数
     */
    public static final long VIEW_RECORD_EXPIRE = 60 * 60;

    /**
     * 首页文章默认每页数量
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 文章不存在
     */
    public static final String ARTICLE_NOT_FOUND = "文章不存在";

}
