package com.lsstop.utils;

import com.lsstop.constant.EmojiConst;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 表情工具类
 *
 * @author lss
 * @date 2026/2/15
 */
public class EmojiUtils {

    private static final Pattern EMOJI_PATTERN = Pattern.compile("\\[.+?]");
    private static final String IMG_TEMPLATE = "<img src=\"%s\" style=\"width:20px;height:20px;vertical-align:middle;\" alt=\"%s\" />";

    private EmojiUtils() {
    }

    /**
     * 将表情标记转换为img标签（用于邮件HTML）
     * 先转义HTML防止XSS，再替换表情为img标签
     *
     * @param content 原始内容
     * @return 转换后的内容
     */
    public static String toHtml(String content) {
        if (content == null || content.isEmpty()) {
            return content;
        }
        // 先转义HTML特殊字符，防止XSS
        String escaped = escapeHtml(content);
        // 再替换表情为img标签
        Matcher matcher = EMOJI_PATTERN.matcher(escaped);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String emoji = matcher.group();
            String url = EmojiConst.EMOJI_MAP.get(emoji);
            // 找到映射则替换为img标签，否则保留原始表情文本
            String replacement = url != null
                    ? String.format(IMG_TEMPLATE, url, escapeHtml(emoji))
                    : Matcher.quoteReplacement(emoji);
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * 转义HTML特殊字符
     */
    private static String escapeHtml(String text) {
        if (text == null) {
            return null;
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
