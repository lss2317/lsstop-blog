package com.lsstop.utils;

import com.alibaba.fastjson2.JSON;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * IP工具类
 * <p>
 * 提供获取客户端IP地址、IP归属地解析、访问设备信息解析等功能
 * </p>
 *
 * @author lishusheng
 * @date 2025/12/23
 */
@Slf4j
public class IpUtils {

    private static final String UNKNOWN = "unknown";
    private static final String LOCAL_IPV6 = "0:0:0:0:0:0:0:1";
    private static final String LOCAL_IPV4 = "127.0.0.1";
    private static final String[] IP_HEADERS = {
            "X-Forwarded-For", "x-forwarded-for", "Proxy-Client-IP",
            "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR", "X-Real-IP"
    };

    private IpUtils() {
    }

    /**
     * 获取客户端IP地址
     *
     * @param request 请求对象
     * @return IP地址
     */
    public static String getIpAddress(HttpServletRequest request) {
        if (request == null) {
            return UNKNOWN;
        }
        String ip = null;
        for (String header : IP_HEADERS) {
            ip = request.getHeader(header);
            if (isValidIp(ip)) {
                break;
            }
        }
        if (!isValidIp(ip)) {
            ip = request.getRemoteAddr();
        }
        if (LOCAL_IPV6.equals(ip)) {
            ip = LOCAL_IPV4;
        }
        return getMultistageReverseProxyIp(ip);
    }

    /**
     * 检测IP地址是否有效
     */
    private static boolean isValidIp(String ip) {
        return StringUtils.isNotBlank(ip) && !UNKNOWN.equalsIgnoreCase(ip);
    }

    /**
     * 解析IP地址归属地
     *
     * @param ipAddress IP地址
     * @return IP归属地信息
     */
    public static String getIpSource(String ipAddress) {
        if (StringUtils.isBlank(ipAddress) || LOCAL_IPV4.equals(ipAddress)) {
            return "本地";
        }
        try {
            URL url = new URL("http://opendata.baidu.com/api.php?query=" + ipAddress + "&co=&resource_id=6006&oe=utf8");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                Map<String, Object> map = JSON.parseObject(result.toString());
                @SuppressWarnings("unchecked")
                List<Map<String, String>> data = (List<Map<String, String>>) map.get("data");
                if (data != null && !data.isEmpty()) {
                    return data.get(0).get("location");
                }
            }
        } catch (Exception e) {
            log.warn("IP地址解析失败: {}", ipAddress);
        }
        return UNKNOWN;
    }

    /**
     * 获取用户代理信息
     *
     * @param request 请求对象
     * @return UserAgent对象
     */
    public static UserAgent getUserAgent(HttpServletRequest request) {
        return UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
    }

    /**
     * 获取浏览器名称
     *
     * @param request 请求对象
     * @return 浏览器名称
     */
    public static String getBrowserName(HttpServletRequest request) {
        Browser browser = getUserAgent(request).getBrowser();
        return browser != null ? browser.getName() : UNKNOWN;
    }

    /**
     * 获取操作系统名称
     *
     * @param request 请求对象
     * @return 操作系统名称
     */
    public static String getOsName(HttpServletRequest request) {
        OperatingSystem os = getUserAgent(request).getOperatingSystem();
        return os != null ? os.getName() : UNKNOWN;
    }

    /**
     * 获取访问设备信息（浏览器 + 操作系统）
     *
     * @param request 请求对象
     * @return 设备信息，格式为 "浏览器 | 操作系统"
     */
    public static String getDeviceInfo(HttpServletRequest request) {
        return getBrowserName(request) + " | " + getOsName(request);
    }

    /**
     * 从多级反向代理中获得第一个非unknown IP地址
     *
     * @param ip 获得的IP地址
     * @return 第一个非unknown IP地址
     */
    private static String getMultistageReverseProxyIp(String ip) {
        if (ip != null && ip.contains(",")) {
            String[] ips = ip.trim().split(",");
            for (String subIp : ips) {
                String trimmedIp = subIp.trim();
                if (isValidIp(trimmedIp)) {
                    return trimmedIp;
                }
            }
        }
        return ip;
    }
}
