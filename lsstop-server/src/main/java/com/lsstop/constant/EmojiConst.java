package com.lsstop.constant;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * 新表情 token 与邮件图片文件名映射。
 */
public final class EmojiConst {

    public static final Map<String, String> EMOJI_MAP = load();

    private EmojiConst() {
    }

    private static Map<String, String> load() {
        try (InputStream input = EmojiConst.class.getResourceAsStream("/emoji/emoji-mail-map.json")) {
            return new ObjectMapper().readValue(input, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new IllegalStateException("读取表情映射失败", e);
        }
    }
}
