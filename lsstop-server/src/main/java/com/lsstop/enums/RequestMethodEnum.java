package com.lsstop.enums;

/**
 * HTTP请求方法枚举
 * <p>仅包含接口权限系统支持的请求方法
 *
 * @author lishusheng
 * @date 2026/06/13
 */
public enum RequestMethodEnum {

    GET,
    POST,
    PUT,
    DELETE;

    /**
     * 校验是否为合法的请求方法
     *
     * @param method 请求方法字符串
     * @return 合法返回true，否则返回false
     */
    public static boolean isValid(String method) {
        if (method == null || method.isBlank()) {
            return false;
        }
        try {
            valueOf(method.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
