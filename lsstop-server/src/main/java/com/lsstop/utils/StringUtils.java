package com.lsstop.utils;

import java.net.URI;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 * @author lishusheng
 * @date 2025/12/23
 */
public class StringUtils {

    /**
     * 上下文摘要默认长度
     */
    private static final int DEFAULT_CONTEXT_LENGTH = 100;

    /**
     * 加粗/斜体正则模式（需要保留内部文本）
     */
    private static final Pattern BOLD_ITALIC_PATTERN = Pattern.compile(
            "[*_]{1,3}([^*_]+)[*_]{1,3}"
    );

    /**
     * Markdown标识符正则模式（预编译提高性能）
     */
    private static final Pattern MARKDOWN_PATTERN = Pattern.compile(
            "```[\\s\\S]*?```" +           // 代码块
                    "|`[^`]+`" +                    // 行内代码
                    "|!?\\[[^\\]]*\\]\\([^)]*\\)" + // 链接和图片
                    "|#{1,6}\\s*" +                 // 标题
                    "|>+\\s*" +                     // 引用
                    "|[-*+]\\s+" +                  // 无序列表
                    "|\\d+\\.\\s+" +                // 有序列表
                    "|\\|" +                        // 表格分隔符
                    "|[-]{3,}" +                    // 分隔线
                    "|\\\\n"                         // 转义换行
    );

    /**
     * 判断字符串是否为空（null、空字符串、纯空白）
     *
     * @param value 字符串
     * @return true-为空，false-不为空
     */
    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    /**
     * 截断文本
     *
     * @param text      文本
     * @param maxLength 最大长度
     * @return 截断后的文本
     */
    public static String truncate(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }

    /**
     * 去除Markdown语法标识符
     *
     * @param text Markdown文本
     * @return 纯文本内容
     */
    public static String stripMarkdown(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        // 先处理加粗/斜体，保留内部文本
        String result = BOLD_ITALIC_PATTERN.matcher(text).replaceAll("$1");
        // 再去除其他Markdown标识符
        return MARKDOWN_PATTERN.matcher(result)
                .replaceAll("")
                .replaceAll("\\s+", " ")
                .trim();
    }

    /**
     * 提取关键词所在位置的上下文摘要
     *
     * @param text    文本内容
     * @param keyword 关键词
     * @return 上下文摘要
     */
    public static String extractContext(String text, String keyword) {
        return extractContext(text, keyword, DEFAULT_CONTEXT_LENGTH);
    }

    /**
     * 提取关键词所在位置的上下文摘要
     *
     * @param text          文本内容
     * @param keyword       关键词
     * @param contextLength 上下文长度（关键词前后各取的字符数）
     * @return 上下文摘要
     */
    public static String extractContext(String text, String keyword, int contextLength) {
        if (text == null || text.isEmpty() || keyword == null || keyword.isEmpty()) {
            return "";
        }
        // 先去除Markdown标识符
        String plainText = stripMarkdown(text);
        // 忽略大小写查找关键词位置
        int index = plainText.toLowerCase().indexOf(keyword.toLowerCase());
        if (index == -1) {
            // 未找到关键词，返回文本开头部分
            return truncate(plainText, contextLength * 2);
        }
        // 计算上下文范围
        int start = Math.max(0, index - contextLength);
        int end = Math.min(plainText.length(), index + keyword.length() + contextLength);
        return plainText.substring(start, end);
    }

    /**
     * 邮箱脱敏处理
     * <p>例如：example@qq.com -> exa***@qq.com</p>
     *
     * @param email 原始邮箱
     * @return 脱敏后的邮箱
     */
    public static String maskEmail(String email) {
        if (email == null || email.isEmpty()) {
            return email;
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return email;
        }
        String prefix = email.substring(0, atIndex);
        String suffix = email.substring(atIndex);
        if (prefix.length() <= 3) {
            return prefix.charAt(0) + "***" + suffix;
        }
        return prefix.substring(0, 3) + "***" + suffix;
    }

    /**
     * 校验URL格式是否合法
     * <p>合法URL必须以 http:// 或 https:// 开头，且包含有效的host</p>
     *
     * @param url URL字符串
     * @return true-合法，false-不合法
     */
    public static boolean isValidUrl(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }
        String trimmedUrl = url.trim();
        try {
            URI uri = URI.create(trimmedUrl);
            String scheme = uri.getScheme();
            if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
                return false;
            }
            // 检查host是否存在且不为空
            String host = uri.getHost();
            return host != null && !host.isBlank();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 校验WebSocket基础地址是否合法
     * <p>支持 ws://、wss://，也支持省略协议的主机地址，例如 localhost:8080</p>
     *
     * @param url WebSocket基础地址
     * @return true-合法，false-不合法
     */
    public static boolean isValidWebSocketUrl(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }

        String trimmedUrl = url.trim();
        boolean hasWebSocketScheme = trimmedUrl.regionMatches(true, 0, "ws://", 0, 5)
                || trimmedUrl.regionMatches(true, 0, "wss://", 0, 6);
        if (!hasWebSocketScheme && trimmedUrl.contains("://")) {
            return false;
        }

        String normalizedUrl = hasWebSocketScheme ? trimmedUrl : "ws://" + trimmedUrl;
        try {
            URI uri = URI.create(normalizedUrl);
            String scheme = uri.getScheme();
            if (!"ws".equalsIgnoreCase(scheme) && !"wss".equalsIgnoreCase(scheme)) {
                return false;
            }

            int port = uri.getPort();
            return uri.getHost() != null
                    && !uri.getHost().isBlank()
                    && (port == -1 || port > 0 && port <= 65535)
                    && uri.getUserInfo() == null
                    && uri.getFragment() == null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 格式化百分比数值为带符号字符串
     * <p>例如：20 → "+20%"、-12 → "-12%"、0 → "+0%"</p>
     *
     * @param percent 百分比数值
     * @return 格式化后的百分比字符串
     */
    public static String formatPercent(int percent) {
        return (percent >= 0 ? "+" : "") + percent + "%";
    }
}
