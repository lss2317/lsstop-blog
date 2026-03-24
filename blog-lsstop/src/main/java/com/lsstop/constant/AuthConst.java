package com.lsstop.constant;

/**
 * 认证相关常量
 *
 * @author lishusheng
 * @date 2026/01/03
 */
public class AuthConst {

    /**
     * 认证请求头
     */
    public static final String AUTH_HEADER = "Authorization";

    /**
     * Token前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 前台来源标识
     */
    public static final String SOURCE_FRONT = "front";

    /**
     * 后台来源标识
     */
    public static final String SOURCE_ADMIN = "admin";

    /**
     * 用户状态：正常
     */
    public static final Integer USER_STATUS_NORMAL = 1;

    /**
     * 用户状态：禁用
     */
    public static final Integer USER_STATUS_DISABLED = 0;

    /**
     * 账号或密码错误
     */
    public static final String ACCOUNT_OR_PASSWORD_ERROR = "账号或密码错误";

    /**
     * 用户不存在（非登录场景使用）
     */
    public static final String USER_NOT_FOUND = "用户不存在";

    /**
     * 账号已被禁用
     */
    public static final String ACCOUNT_DISABLED = "该账号已被禁用";

    /**
     * 登录成功
     */
    public static final String LOGIN_SUCCESS = "登录成功";

    /**
     * refreshToken无效
     */
    public static final String REFRESH_TOKEN_INVALID = "refreshToken无效";

    /**
     * refreshToken已过期
     */
    public static final String REFRESH_TOKEN_EXPIRED = "refreshToken已过期";

    /**
     * 未登录或Token缺失
     */
    public static final String TOKEN_MISSING = "未登录或Token已过期";

    /**
     * Token无效或已过期
     */
    public static final String TOKEN_INVALID = "Token无效或已过期";

    /**
     * 无权限访问后台接口
     */
    public static final String NO_ADMIN_ACCESS = "无权限访问后台接口";

    /**
     * 请使用前台账号访问
     */
    public static final String FRONT_ACCOUNT_REQUIRED = "请使用前台账号访问";

    /**
     * 无效的验证码用途
     */
    public static final String INVALID_CODE_PURPOSE = "无效的验证码用途";

    /**
     * 邮箱已被注册
     */
    public static final String EMAIL_ALREADY_REGISTERED = "该邮箱已被注册";

    /**
     * 验证码发送太频繁
     */
    public static final String CODE_SEND_TOO_FREQUENT = "验证码发送太频繁，请稍后重试";

    /**
     * 验证码过期时间（分钟）
     */
    public static final int CODE_EXPIRE_MINUTES = 5;

    /**
     * 验证码重发间隔（秒）
     */
    public static final int CODE_RESEND_INTERVAL_SECONDS = 60;

    /**
     * 验证码错误或已过期
     */
    public static final String CODE_INVALID_OR_EXPIRED = "验证码错误或已过期";

    /**
     * 新密码不能与原密码相同
     */
    public static final String NEW_PASSWORD_SAME_AS_OLD = "新密码不能与原密码相同";

    /**
     * 密码不能包含空格
     */
    public static final String PASSWORD_CONTAINS_WHITESPACE = "密码不能包含空格";

    /**
     * 密码不能为空
     */
    public static final String PASSWORD_NOT_EMPTY = "密码不能为空";

    /**
     * 密码长度不符合要求
     */
    public static final String PASSWORD_LENGTH_INVALID = "密码长度为6-20位";

    /**
     * 密码最小长度
     */
    public static final int PASSWORD_MIN_LENGTH = 6;

    /**
     * 密码最大长度
     */
    public static final int PASSWORD_MAX_LENGTH = 20;

    /**
     * 新邮箱不能与原邮箱相同
     */
    public static final String NEW_EMAIL_SAME_AS_OLD = "新邮箱不能与原邮箱相同";

    /**
     * 邮箱修改成功
     */
    public static final String EMAIL_CHANGE_SUCCESS = "邮箱修改成功";

    /**
     * 个人网站格式不正确
     */
    public static final String WEBSITE_FORMAT_INVALID = "个人网站格式不正确";

    /**
     * 旧密码错误
     */
    public static final String OLD_PASSWORD_ERROR = "旧密码错误";

    /**
     * QQ获取openId接口
     */
    public static final String QQ_OPENID_URL = "https://graph.qq.com/oauth2.0/me?access_token=%s&fmt=json";

    /**
     * QQ获取access_token接口
     */
    public static final String QQ_ACCESS_TOKEN_URL = "https://graph.qq.com/oauth2.0/token?grant_type=authorization_code&client_id=%s&client_secret=%s&code=%s&redirect_uri=%s&fmt=json";

    /**
     * 微博获取access_token接口
     */
    public static final String WEIBO_ACCESS_TOKEN_URL = "https://api.weibo.com/oauth2/access_token";

    /**
     * QQ响应字段 access_token
     */
    public static final String QQ_RESPONSE_ACCESS_TOKEN = "access_token";

    /**
     * 微博响应字段 access_token
     */
    public static final String WEIBO_RESPONSE_ACCESS_TOKEN = "access_token";

    /**
     * QQ认证失败
     */
    public static final String QQ_AUTH_FAILED = "QQ认证失败";

    /**
     * 微博认证失败
     */
    public static final String WEIBO_AUTH_FAILED = "微博认证失败";

    /**
     * QQ账号未绑定
     */
    public static final String QQ_NOT_BINDDED = "该QQ账号未绑定，请先注册并绑定";

    /**
     * 微博账号未绑定
     */
    public static final String WEIBO_NOT_BINDDED = "该微博账号未绑定，请先注册并绑定";

    /**
     * QQ登录失败
     */
    public static final String QQ_LOGIN_FAILED = "QQ登录失败，请重试";

    /**
     * 微博登录失败
     */
    public static final String WEIBO_LOGIN_FAILED = "微博登录失败，请重试";

    /**
     * QQ响应字段 openid
     */
    public static final String QQ_RESPONSE_OPENID = "openid";

    /**
     * 微博响应字段 uid
     */
    public static final String WEIBO_RESPONSE_UID = "uid";

    /**
     * QQ账号已被其他用户绑定
     */
    public static final String QQ_ALREADY_BINDDED_BY_OTHER = "该QQ账号已被其他用户绑定";

    /**
     * 微博账号已被其他用户绑定
     */
    public static final String WEIBO_ALREADY_BINDDED_BY_OTHER = "该微博账号已被其他用户绑定";

    /**
     * 已绑定QQ
     */
    public static final String QQ_ALREADY_BINDDED = "您已绑定QQ账号";

    /**
     * 已绑定微博
     */
    public static final String WEIBO_ALREADY_BINDDED = "您已绑定微博账号";

    /**
     * QQ绑定失败
     */
    public static final String QQ_BIND_FAILED = "QQ绑定失败，请重试";

    /**
     * 微博绑定失败
     */
    public static final String WEIBO_BIND_FAILED = "微博绑定失败，请重试";

    /**
     * 未绑定QQ
     */
    public static final String QQ_NOT_BINDDED_YET = "您尚未绑定QQ账号";

    /**
     * 未绑定微博
     */
    public static final String WEIBO_NOT_BINDDED_YET = "您尚未绑定微博账号";

    /**
     * QQ解绑失败
     */
    public static final String QQ_UNBIND_FAILED = "QQ解绑失败，请重试";

    /**
     * 微博解绑失败
     */
    public static final String WEIBO_UNBIND_FAILED = "微博解绑失败，请重试";
}
