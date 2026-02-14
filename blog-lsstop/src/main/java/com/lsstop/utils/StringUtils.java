package com.lsstop.utils;

/**
 * 字符串工具类
 *
 * @author lishusheng
 * @date 2025/12/23
 */
public class StringUtils {

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
}
