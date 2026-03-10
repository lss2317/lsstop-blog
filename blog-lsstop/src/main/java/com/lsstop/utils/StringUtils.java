package com.lsstop.utils;

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
     * Markdown标识符正则模式（预编译提高性能）
     */
    private static final Pattern MARKDOWN_PATTERN = Pattern.compile(
            "```[\\s\\S]*?```" +           // 代码块
            "|`[^`]+`" +                    // 行内代码
            "|!?\\[[^\\]]*\\]\\([^)]*\\)" + // 链接和图片
            "|#{1,6}\\s*" +                 // 标题
            "|[*_]{1,3}([^*_]+)[*_]{1,3}" + // 加粗/斜体
            "|>+\\s*" +                     // 引用
            "|[-*+]\\s+" +                  // 无序列表
            "|\\d+\\.\\s+" +                // 有序列表
            "|\\|" +                        // 表格分隔符
            "|[-]{3,}" +                    // 分隔线
            "|\\\\n"                         // 转义换行
    );

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
        return MARKDOWN_PATTERN.matcher(text)
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
}
