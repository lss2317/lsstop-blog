package com.lsstop.utils;

import java.util.regex.Pattern;

/**
 * 格式校验工具类
 *
 * @author lishusheng
 */
public class ValidateUtils {

    /**
     * 路由地址：字母、数字、-、_、/、:
     */
    private static final Pattern PATH_PATTERN = Pattern.compile("^[a-zA-Z0-9\\-_/:]+$");

    /**
     * 路由标识：PascalCase，大写字母开头
     */
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Z][a-zA-Z0-9]*$");

    /**
     * 组件路径：以/开头，字母、数字、-、_、/
     */
    private static final Pattern COMPONENT_PATTERN = Pattern.compile("^/[a-zA-Z0-9\\-_/]+$");

    /**
     * 权限标识：小写字母开头，小写字母、数字、下划线
     */
    private static final Pattern AUTH_MARK_PATTERN = Pattern.compile("^[a-z][a-z0-9_]*$");

    /**
     * 图标：iconify格式，前缀:图标名，如 ri:user-line
     */
    private static final Pattern ICON_PATTERN = Pattern.compile("^[a-z][a-z0-9]*:[a-zA-Z0-9\\-_]+$");

    /**
     * 接口路径：字母、数字、-、_、/、:、*、.（支持Ant风格通配符）
     */
    private static final Pattern REQUEST_URL_PATTERN = Pattern.compile("^[a-zA-Z0-9\\-_/.:*]+$");

    /**
     * 校验路由地址格式
     */
    public static boolean isValidPath(String path) {
        return path != null && PATH_PATTERN.matcher(path).matches() && !path.contains("//");
    }

    /**
     * 校验路由标识格式（PascalCase）
     */
    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name).matches();
    }

    /**
     * 校验组件路径格式
     */
    public static boolean isValidComponent(String component) {
        return component != null && COMPONENT_PATTERN.matcher(component).matches();
    }

    /**
     * 校验权限标识格式
     */
    public static boolean isValidAuthMark(String authMark) {
        return authMark != null && AUTH_MARK_PATTERN.matcher(authMark).matches();
    }

    /**
     * 校验图标格式（iconify，如 ri:user-line）
     */
    public static boolean isValidIcon(String icon) {
        return icon != null && ICON_PATTERN.matcher(icon).matches();
    }

    /**
     * 校验接口路径格式（支持Ant风格通配符，如 /admin/user/list、/front/user/profile/*）
     * <p>必须以/开头，不允许连续斜杠，允许字母、数字、-、_、/、:、*、.
     */
    public static boolean isValidRequestUrl(String requestUrl) {
        return requestUrl != null
                && requestUrl.startsWith("/")
                && !requestUrl.contains("//")
                && REQUEST_URL_PATTERN.matcher(requestUrl).matches();
    }

}
