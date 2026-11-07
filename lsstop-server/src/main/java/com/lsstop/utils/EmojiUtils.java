package com.lsstop.utils;

import com.lsstop.constant.EmojiConst;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 将评论中的表情 token 转成邮件 HTML。
 */
public final class EmojiUtils {

    private static final Pattern EMOJI_PATTERN = Pattern.compile("\\[[^\\[\\]\\r\\n]{1,32}]");
    private static final String IMG_TEMPLATE = "<img src=\"%s\" width=\"20\" height=\"20\" "
            + "style=\"vertical-align:middle;\" alt=\"%s\" />";

    private EmojiUtils() {
    }

    public static String toHtml(String content, String emojiBaseUrl) {
        if (content == null || content.isEmpty()) {
            return content;
        }

        String baseUrl = emojiBaseUrl.replaceAll("/+$", "");
        Matcher matcher = EMOJI_PATTERN.matcher(escapeHtml(content));
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String token = matcher.group();
            String fileName = EmojiConst.EMOJI_MAP.get(token);
            String replacement = fileName == null
                    ? token
                    : String.format(IMG_TEMPLATE, baseUrl + "/" + fileName, token);
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
