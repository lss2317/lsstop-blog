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
}
