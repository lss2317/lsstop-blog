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
     * 今日独立访客IP集合（Set结构）
     */
    public static final String TODAY_UV_SET = PREFIX + "visitor:today:uv:";

    /**
     * 历史总访问量
     */
    public static final String HISTORY_VIEW_COUNT = PREFIX + "visitor:history:count";

    /**
     * 历史日期访问量缓存（按日期缓存，如 blog:visitor:daily:20260509）
     */
    public static final String DAILY_VIEW_COUNT = PREFIX + "visitor:daily:";

    /**
     * 今日新增评论数（如 blog:comment:today:20260510）
     */
    public static final String TODAY_COMMENT_COUNT = PREFIX + "comment:today:";

    /**
     * 历史日期评论数缓存（如 blog:comment:daily:20260509）
     */
    public static final String DAILY_COMMENT_COUNT = PREFIX + "comment:daily:";

    /**
     * 今日新增留言数（如 blog:message:today:20260510）
     */
    public static final String TODAY_MESSAGE_COUNT = PREFIX + "message:today:";

    /**
     * 历史日期留言数缓存（如 blog:message:daily:20260509）
     */
    public static final String DAILY_MESSAGE_COUNT = PREFIX + "message:daily:";

    /**
     * 今日新增点赞数（如 blog:like:today:20260510）
     */
    public static final String TODAY_LIKE_COUNT = PREFIX + "like:today:";

    /**
     * 历史日期点赞数缓存（如 blog:like:daily:20260509）
     */
    public static final String DAILY_LIKE_COUNT = PREFIX + "like:daily:";

    /**
     * 历史日期独立访客数缓存（如 blog:visitor:daily:uv:20260509）
     */
    public static final String DAILY_UV_COUNT = PREFIX + "visitor:daily:uv:";

    /**
     * 今日新增用户数（如 blog:user:today:20260609）
     */
    public static final String TODAY_USER_COUNT = PREFIX + "user:today:";

    /**
     * 用户总数缓存
     */
    public static final String TOTAL_USER_COUNT = PREFIX + "user:total:count";

    /**
     * 评论总数缓存
     */
    public static final String TOTAL_COMMENT_COUNT = PREFIX + "comment:total:count";

    /**
     * 留言总数缓存
     */
    public static final String TOTAL_MESSAGE_COUNT = PREFIX + "message:total:count";

    /**
     * 文章总数缓存
     */
    public static final String TOTAL_ARTICLE_COUNT = PREFIX + "article:total:count";

    /**
     * 分类总数缓存
     */
    public static final String TOTAL_CATEGORY_COUNT = PREFIX + "category:total:count";

    /**
     * 标签总数缓存
     */
    public static final String TOTAL_TAG_COUNT = PREFIX + "tag:total:count";

    /**
     * 友链总数缓存
     */
    public static final String TOTAL_FRIEND_LINK_COUNT = PREFIX + "friendLink:total:count";

    /**
     * 待审核评论数缓存
     */
    public static final String PENDING_REVIEW_COMMENT_COUNT = PREFIX + "comment:pendingReview:count";

    /**
     * 待审核留言数缓存
     */
    public static final String PENDING_REVIEW_MESSAGE_COUNT = PREFIX + "message:pendingReview:count";

    /**
     * 文章浏览量
     */
    public static final String ARTICLE_VIEW_COUNT = PREFIX + "article:viewCount:";

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
     * 用户公开主页缓存
     */
    public static final String USER_HOME_PUBLIC = PREFIX + "user:home:public:";

    /**
     * 用户个人主页缓存
     */
    public static final String USER_HOME_ME = PREFIX + "user:home:me:";

    /**
     * 前台用户RefreshToken
     */
    public static final String FRONT_REFRESH_TOKEN = PREFIX + "front:refresh:token:";

    /**
     * 后台用户RefreshToken
     */
    public static final String ADMIN_REFRESH_TOKEN = PREFIX + "admin:refresh:token:";

    /**
     * 用户菜单树缓存
     */
    public static final String USER_MENU_TREE = PREFIX + "user:menu:tree:";

    /**
     * 用户菜单ID列表缓存
     */
    public static final String USER_MENU_IDS = PREFIX + "user:menu:ids:";

    /**
     * 用户有效接口权限缓存（Set<String>，格式：METHOD:/uri/pattern）
     * <p>基于 blog_api_permission 三表体系计算：角色授予 ∪ 用户额外授予 - 用户额外排除
     */
    public static final String USER_EFFECTIVE_API_PERMISSIONS = PREFIX + "user:effective:api:permissions:";

    /**
     * 用户有效接口权限ID列表缓存
     */
    public static final String USER_API_PERMISSION_IDS = PREFIX + "user:api:permission:ids:";

    /**
     * 系统全量已注册接口权限缓存（Set<String>，格式：METHOD:/uri/pattern）
     * <p>用于判断当前请求是否受权限控制
     */
    public static final String REGISTERED_API_PERMISSIONS = PREFIX + "registered:api:permissions";

    /**
     * 全量菜单权限树缓存（用于权限配置弹窗）
     */
    public static final String MENU_PERMISSION_TREE = PREFIX + "menu:permission:tree";

    /**
     * 全量接口权限树缓存
     */
    public static final String API_PERMISSION_TREE = PREFIX + "api:permission:tree";

    /**
     * 角色选项列表缓存（下拉选择用）
     */
    public static final String ROLE_OPTIONS = PREFIX + "role:options";

    /**
     * 页面信息列表缓存
     */
    public static final String PAGE_INFO_LIST = PREFIX + "page:info:list";

    /**
     * 仪表盘-热门文章缓存
     */
    public static final String DASHBOARD_TOP_ARTICLES = PREFIX + "dashboard:topArticles";

    /**
     * 仪表盘-分类分布缓存
     */
    public static final String DASHBOARD_CATEGORY_DISTRIBUTION = PREFIX + "dashboard:categoryDistribution";

    /**
     * 仪表盘-评论来源分布缓存
     */
    public static final String DASHBOARD_COMMENT_SOURCE = PREFIX + "dashboard:commentSource";

    /**
     * 仪表盘-标签热度缓存
     */
    public static final String DASHBOARD_TAG_RADAR = PREFIX + "dashboard:tagRadar";

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
     * 过期时间：2小时（秒）
     */
    public static final long EXPIRE_TWO_HOURS = 2 * 60 * 60;

    /**
     * 过期时间：1天（秒）
     */
    public static final long EXPIRE_ONE_DAY = 24 * 60 * 60;

    /**
     * 过期时间：1周（秒）
     */
    public static final long EXPIRE_ONE_WEEK = 7 * 24 * 60 * 60;
}
