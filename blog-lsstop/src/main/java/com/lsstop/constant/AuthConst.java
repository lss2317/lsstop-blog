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
     * 未知
     */
    public static final String UNKNOWN = "unknown";
}
