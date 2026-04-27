package com.lsstop.utils;

import com.lsstop.constant.CommonConst;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * IP工具类
 * <p>
 * 提供IP地址获取、IP归属地解析等功能
 *
 * @author lishusheng
 * @date 2025/12/23
 */
@Slf4j
@Component
public class IpUtils {

    private static final String UNKNOWN = "unknown";
    private static final String PLACEHOLDER = "0";

    /**
     * 本地IP
     */
    private static final String LOCAL_IPV4 = "127.0.0.1";
    private static final String LOCAL_IPV6 = "0:0:0:0:0:0:0:1";
    private static final String LOCAL_LABEL = "本地";

    /**
     * 地区解析
     */
    private static final String COUNTRY_CHINA = "中国";
    private static final String SUFFIX_PROVINCE = "省";
    private static final String SUFFIX_CITY = "市";

    /**
     * IP请求头（按优先级排序）
     */
    private static final String[] IP_HEADERS = {
            "X-Forwarded-For", "x-forwarded-for", "Proxy-Client-IP",
            "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR", "X-Real-IP"
    };

    private static Searcher SEARCHER;
    private static File tempXdbFile;
    private static String initErrorMsg;

    static {
        initSearcher();
    }

    private static void initSearcher() {
        try {
            File tempFile = extractXdbToTemp();
            if (tempFile == null) {
                initErrorMsg = "ip2region.xdb文件未找到，请确保文件存在于src/main/resources/ip2region.xdb";
                log.error(initErrorMsg);
                return;
            }
            tempXdbFile = tempFile;
            String dbPath = tempFile.getAbsolutePath();
            byte[] vIndex = Searcher.loadVectorIndexFromFile(dbPath);
            SEARCHER = Searcher.newWithVectorIndex(dbPath, vIndex);
            log.info("ip2region数据库加载成功（VectorIndex模式）");
        } catch (IOException e) {
            initErrorMsg = "ip2region数据库文件读取失败: " + e.getMessage();
            log.error(initErrorMsg, e);
        } catch (Exception e) {
            initErrorMsg = "ip2region数据库初始化失败: " + e.getMessage();
            log.error(initErrorMsg, e);
        }
    }

    @PreDestroy
    public void destroy() {
        closeSearcher();
        deleteTempFile();
    }

    private void closeSearcher() {
        if (SEARCHER == null) {
            return;
        }
        try {
            SEARCHER.close();
            log.info("ip2region Searcher资源已关闭");
        } catch (Exception e) {
            log.error("关闭ip2region Searcher失败", e);
        }
    }

    private void deleteTempFile() {
        if (tempXdbFile != null && tempXdbFile.exists() && tempXdbFile.delete()) {
            log.info("ip2region临时文件已删除");
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
            return CommonConst.UNKNOWN;
        }
        String ip = getIpFromHeaders(request);
        if (!isValidIp(ip)) {
            ip = request.getRemoteAddr();
        }
        if (LOCAL_IPV6.equals(ip)) {
            ip = LOCAL_IPV4;
        }
        return extractFirstIp(ip);
    }

    /**
     * 获取IP所在地（省份/地区）
     *
     * @param ipAddress IP地址
     * @return 省份/地区名称
     */
    public static String getIpLocation(String ipAddress) {
        if (StringUtils.isBlank(ipAddress) || LOCAL_IPV4.equals(ipAddress)) {
            return LOCAL_LABEL;
        }
        if (SEARCHER == null) {
            log.debug("Searcher未初始化，无法解析IP: {}. 错误: {}", ipAddress, initErrorMsg);
            return CommonConst.UNKNOWN;
        }
        try {
            String region = SEARCHER.search(ipAddress);
            return parseProvince(region);
        } catch (IOException e) {
            log.warn("IP地址解析IO异常: {}, 错误: {}", ipAddress, e.getMessage());
        } catch (Exception e) {
            log.warn("IP地址解析失败: {}, 错误: {}", ipAddress, e.getMessage());
        }
        return CommonConst.UNKNOWN;
    }

    private static String getIpFromHeaders(HttpServletRequest request) {
        for (String header : IP_HEADERS) {
            String ip = request.getHeader(header);
            if (isValidIp(ip)) {
                return ip;
            }
        }
        return null;
    }

    private static String extractFirstIp(String ip) {
        if (ip != null && ip.contains(",")) {
            for (String subIp : ip.trim().split(",")) {
                String trimmedIp = subIp.trim();
                if (isValidIp(trimmedIp)) {
                    return trimmedIp;
                }
            }
        }
        return ip;
    }

    private static boolean isValidIp(String ip) {
        return StringUtils.isNotBlank(ip) && !UNKNOWN.equalsIgnoreCase(ip);
    }

    /**
     * 从ip2region结果中提取省份/直辖市名称
     * ip2region格式：国家|区域|省份|城市|ISP
     */
    private static String parseProvince(String region) {
        if (StringUtils.isBlank(region) || PLACEHOLDER.equals(region)) {
            return CommonConst.UNKNOWN;
        }
        String[] parts = region.split("\\|");
        String country = getPart(parts, 0);
        String province = getPart(parts, 1);

        // 国家不是中国，直接返回国家名
        if (isValidPart(country) && !COUNTRY_CHINA.equals(country)) {
            return country;
        }
        // 省份有效，处理后返回
        if (isValidPart(province)) {
            return trimSuffix(province);
        }
        // 省份无效时，返回国家
        return isValidPart(country) ? country : CommonConst.UNKNOWN;
    }

    private static String getPart(String[] parts, int index) {
        return parts.length > index ? parts[index] : PLACEHOLDER;
    }

    private static boolean isValidPart(String part) {
        return StringUtils.isNotBlank(part) && !PLACEHOLDER.equals(part);
    }

    private static String trimSuffix(String name) {
        if (name.endsWith(SUFFIX_PROVINCE) || name.endsWith(SUFFIX_CITY)) {
            return name.substring(0, name.length() - 1);
        }
        return name;
    }

    private static File extractXdbToTemp() throws IOException {
        try (InputStream is = IpUtils.class.getResourceAsStream("/ip2region.xdb")) {
            if (is == null) {
                return null;
            }
            File tempFile = new File(System.getProperty("java.io.tmpdir"), "ip2region.xdb");
            byte[] resourceBytes = is.readAllBytes();

            // 判断文件是否已存在且MD5一致
            if (tempFile.exists() && calculateMd5(resourceBytes).equals(getFileMd5(tempFile))) {
                log.debug("使用已存在的临时文件: {}", tempFile.getAbsolutePath());
                return tempFile;
            }

            try (OutputStream os = new FileOutputStream(tempFile)) {
                os.write(resourceBytes);
            }
            tempFile.deleteOnExit();
            log.debug("提取ip2region.xdb到临时目录: {}", tempFile.getAbsolutePath());
            return tempFile;
        }
    }

    private static String calculateMd5(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(data);
            StringBuilder sb = new StringBuilder(32);
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("MD5算法不可用", e);
            return "";
        }
    }

    private static String getFileMd5(File file) {
        try {
            return calculateMd5(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            log.warn("读取文件MD5失败: {}", file.getAbsolutePath());
            return "";
        }
    }
}
