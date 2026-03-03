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
     * 邮箱尚未注册
     */
    public static final String EMAIL_NOT_REGISTERED = "该邮箱尚未注册";

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
}
