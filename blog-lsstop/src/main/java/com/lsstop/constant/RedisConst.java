package com.lsstop.constant;

/**
 * Redis常量
 *
 * @author lishusheng
 * @date 2026/01/02
 */
public class RedisConst {

    /**
     * Redis键前缀
     */
    private static final String PREFIX = "blog:";

    /**
     * 网站配置
     */
    public static final String WEBSITE_CONFIG = PREFIX + "website:config";

    /**
     * 独立访客
     */
    public static final String UNIQUE_VISITOR = PREFIX + "visitor:uv:";

    /**
     * 今日访问量
     */
    public static final String TODAY_VIEW_COUNT = PREFIX + "visitor:today:count";

    /**
     * 历史总访问量
     */
    public static final String HISTORY_VIEW_COUNT = PREFIX + "visitor:history:count";

    /**
     * 文章浏览量
     */
    public static final String ARTICLE_VIEW_COUNT = PREFIX + "article:view:";

    /**
     * 文章点赞数
     */
    public static final String ARTICLE_LIKE_COUNT = PREFIX + "article:like:count:";

    /**
     * 最新文章列表缓存
     */
    public static final String NEWEST_ARTICLES = PREFIX + "article:newest";

    /**
     * 文章浏览记录（防刷）
     */
    public static final String ARTICLE_VIEW_RECORD = PREFIX + "article:view:record:";

    /**
     * 说说点赞数
     */
    public static final String TALK_LIKE_COUNT = PREFIX + "talk:like:count:";

    /**
     * 说说评论数
     */
    public static final String TALK_COMMENT_COUNT = PREFIX + "talk:comment:count:";

    /**
     * 评论点赞数
     */
    public static final String COMMENT_LIKE_COUNT = PREFIX + "comment:like:count:";

    /**
     * 评论回复数
     */
    public static final String COMMENT_REPLY_COUNT = PREFIX + "comment:reply:count:";

    /**
     * 用户文章点赞记录
     */
    public static final String USER_ARTICLE_LIKE = PREFIX + "user:article:like:";

    /**
     * 用户说说点赞记录
     */
    public static final String USER_TALK_LIKE = PREFIX + "user:talk:like:";

    /**
     * 用户评论点赞记录
     */
    public static final String USER_COMMENT_LIKE = PREFIX + "user:comment:like:";

    /**
     * 待同步点赞记录（Hash结构，field: userId:targetId，value: 1点赞/0取消）
     */
    public static final String LIKE_PENDING_SYNC = PREFIX + "like:pending:";

    /**
     * 接口限流
     */
    public static final String RATE_LIMIT = PREFIX + "limit:";

    /**
     * 图形验证码
     */
    public static final String CAPTCHA = PREFIX + "captcha:";

    /**
     * 邮箱验证码
     */
    public static final String EMAIL_CODE = PREFIX + "email:code:";

    /**
     * 用户Token
     */
    public static final String USER_TOKEN = PREFIX + "user:token:";

    /**
     * 用户信息缓存
     */
    public static final String USER_INFO = PREFIX + "user:info:";

    /**
     * 前台用户RefreshToken
     */
    public static final String FRONT_REFRESH_TOKEN = PREFIX + "front:refresh:token:";

    /**
     * 后台用户RefreshToken
     */
    public static final String ADMIN_REFRESH_TOKEN = PREFIX + "admin:refresh:token:";

    /**
     * 页面信息列表缓存
     */
    public static final String PAGE_INFO_LIST = PREFIX + "page:info:list";

    /**
     * 过期时间：1分钟（秒）
     */
    public static final long EXPIRE_ONE_MINUTE = 60;

    /**
     * 过期时间：5分钟（秒）
     */
    public static final long EXPIRE_FIVE_MINUTES = 5 * 60;

    /**
     * 过期时间：1小时（秒）
     */
    public static final long EXPIRE_ONE_HOUR = 60 * 60;

    /**
     * 过期时间：1天（秒）
     */
    public static final long EXPIRE_ONE_DAY = 24 * 60 * 60;

    /**
     * 过期时间：1周（秒）
     */
    public static final long EXPIRE_ONE_WEEK = 7 * 24 * 60 * 60;
}
