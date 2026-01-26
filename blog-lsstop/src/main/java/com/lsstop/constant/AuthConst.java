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
     * 用户不存在
     */
    public static final String USER_NOT_FOUND = "用户不存在";

    /**
     * 密码错误
     */
    public static final String PASSWORD_ERROR = "密码错误";

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
}
