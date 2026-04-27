package com.lsstop.utils;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import jakarta.servlet.http.HttpServletRequest;

/**
 * User-Agent 解析工具类
 *
 * @author lishusheng
 * @date 2026/01/17
 */
public class UserAgentUtils {

    private static final String USER_AGENT_HEADER = "User-Agent";

    /**
     * 获取浏览器名称（带版本号）
     *
     * @param request 请求对象
     * @return 浏览器名称和版本
     */
    public static String getBrowser(HttpServletRequest request) {
        UserAgent ua = UserAgentUtil.parse(request.getHeader(USER_AGENT_HEADER));
        String name = ua.getBrowser().getName();
        String version = ua.getVersion();
        return version != null ? name + " " + version : name;
    }

    /**
     * 获取操作系统名称（带版本号）
     *
     * @param request 请求对象
     * @return 操作系统名称和版本
     */
    public static String getOS(HttpServletRequest request) {
        UserAgent ua = UserAgentUtil.parse(request.getHeader(USER_AGENT_HEADER));
        String name = ua.getOs().getName();
        String osVersion = ua.getOsVersion();
        return osVersion != null ? name + " " + osVersion : name;
    }
}
