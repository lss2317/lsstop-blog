package com.lsstop.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lionsoul.ip2region.xdb.Searcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * IP工具类
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

    private static Searcher SEARCHER;

    static {
        try {
            // 从classpath提取xdb文件到临时目录
            File tempFile = extractXdbToTemp();
            if (tempFile != null) {
                String dbPath = tempFile.getAbsolutePath();
                // 加载VectorIndex缓存，减少查询时的IO操作
                byte[] vIndex = Searcher.loadVectorIndexFromFile(dbPath);
                // 使用VectorIndex创建Searcher（推荐方式）
                SEARCHER = Searcher.newWithVectorIndex(dbPath, vIndex);
                log.info("ip2region数据库加载成功（VectorIndex模式）");
            } else {
                log.error("ip2region.xdb文件未找到");
            }
        } catch (Exception e) {
            log.error("ip2region数据库加载失败", e);
        }
    }

    /**
     * 从classpath提取xdb文件到临时目录
     */
    private static File extractXdbToTemp() {
        try (InputStream is = IpUtils.class.getResourceAsStream("/ip2region.xdb")) {
            if (is == null) {
                return null;
            }
            File tempFile = new File(System.getProperty("java.io.tmpdir"), "ip2region.xdb");
            byte[] resourceBytes = is.readAllBytes();
            // 判断文件是否已存在
            if (tempFile.exists() && tempFile.length() == resourceBytes.length) {
                return tempFile;
            }
            try (OutputStream os = new FileOutputStream(tempFile)) {
                os.write(resourceBytes);
            }
            return tempFile;
        } catch (Exception e) {
            log.error("提取ip2region.xdb失败", e);
            return null;
        }
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
     * 获取IP所在地（省份/地区）
     *
     * @param ipAddress IP地址
     * @return 省份/地区名称
     */
    public static String getIpLocation(String ipAddress) {
        if (StringUtils.isBlank(ipAddress) || LOCAL_IPV4.equals(ipAddress)) {
            return "本地";
        }
        if (SEARCHER == null) {
            return UNKNOWN;
        }
        try {
            String region = SEARCHER.search(ipAddress);
            return parseProvince(region);
        } catch (Exception e) {
            log.warn("IP地址解析失败: {}", ipAddress);
        }
        return UNKNOWN;
    }

    /**
     * 从ip2region结果中提取省份/直辖市名称
     * ip2region格式：国家|省份|城市|ISP
     */
    private static String parseProvince(String region) {
        if (StringUtils.isBlank(region) || "0".equals(region)) {
            return UNKNOWN;
        }
        String[] parts = region.split("\\|");
        // 国家不是中国，返回国家名
        if (parts.length > 0 && !"0".equals(parts[0]) && !"中国".equals(parts[0])) {
            return parts[0];
        }
        // 获取省份（第2个字段，index=1）
        String province = parts.length > 1 ? parts[1] : "0";
        if (!"0".equals(province) && StringUtils.isNotBlank(province)) {
            // 去除"省"字
            if (province.endsWith("省")) {
                return province.substring(0, province.length() - 1);
            }
            // 直辖市去除"市"字
            if (province.endsWith("市")) {
                return province.substring(0, province.length() - 1);
            }
            return province;
        }
        // 省份为空时，返回国家（如：中国|0|0|移动 -> 中国）
        if (parts.length > 0 && !"0".equals(parts[0])) {
            return parts[0];
        }
        return UNKNOWN;
    }

    /**
     * 从多级反向代理中获得第一个非unknown IP地址
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
