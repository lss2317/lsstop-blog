package com.lsstop.utils;

import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import com.lsstop.constant.CommonConst;
import com.lsstop.constant.EmojiConst;
import com.lsstop.enums.IllegalPolicyEnum;
import com.lsstop.exception.BusinessException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 敏感词工具类。仅保护新表情清单中存在的完整 token。
 */
public final class SensitiveWordUtils {

    private static final Pattern EMOJI_PATTERN = Pattern.compile("\\[[^\\[\\]\\r\\n]{1,32}]");

    private SensitiveWordUtils() {
    }

    public record Result(boolean hasSensitive, String content) {
    }

    public static boolean contains(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        return SensitiveWordHelper.contains(maskEmojiTokens(text).plainText());
    }

    public static List<String> findAll(String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        return SensitiveWordHelper.findAll(maskEmojiTokens(text).plainText());
    }

    public static String replace(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        MaskedText masked = maskEmojiTokens(text);
        return masked.restore(SensitiveWordHelper.replace(masked.plainText()));
    }

    public static Result process(String text, IllegalPolicyEnum policy) {
        if (text == null || text.isEmpty()) {
            return new Result(false, text);
        }

        MaskedText masked = maskEmojiTokens(text);
        boolean hasSensitive = SensitiveWordHelper.contains(masked.plainText());
        if (!hasSensitive) {
            return new Result(false, text);
        }

        if (IllegalPolicyEnum.BLOCK == policy) {
            throw new BusinessException(CommonConst.SENSITIVE_WORD_DETECTED);
        }
        if (IllegalPolicyEnum.REPLACE == policy) {
            return new Result(true, masked.restore(SensitiveWordHelper.replace(masked.plainText())));
        }
        return new Result(true, text);
    }

    public static void validate(String text) {
        if (contains(text)) {
            throw new BusinessException(CommonConst.SENSITIVE_WORD_DETECTED);
        }
    }

    private static MaskedText maskEmojiTokens(String text) {
        Matcher matcher = EMOJI_PATTERN.matcher(text);
        List<TextPart> parts = new ArrayList<>();
        StringBuilder plainText = new StringBuilder(text.length());
        int cursor = 0;

        while (matcher.find()) {
            String token = matcher.group();
            if (!EmojiConst.EMOJI_MAP.containsKey(token)) {
                continue;
            }
            appendPlainPart(text.substring(cursor, matcher.start()), parts, plainText);
            parts.add(new TextPart(true, token));
            cursor = matcher.end();
        }
        appendPlainPart(text.substring(cursor), parts, plainText);
        return new MaskedText(plainText.toString(), parts);
    }

    private static void appendPlainPart(String value, List<TextPart> parts, StringBuilder plainText) {
        if (value.isEmpty()) {
            return;
        }
        parts.add(new TextPart(false, value));
        plainText.append(value);
    }

    private record TextPart(boolean emoji, String value) {
    }

    private record MaskedText(String plainText, List<TextPart> parts) {
        private String restore(String filteredText) {
            StringBuilder result = new StringBuilder(filteredText.length() + 32);
            int offset = 0;
            for (TextPart part : parts) {
                if (part.emoji()) {
                    result.append(part.value());
                    continue;
                }
                int nextOffset = offset + part.value().length();
                result.append(filteredText, offset, nextOffset);
                offset = nextOffset;
            }
            return result.toString();
        }
    }
}
