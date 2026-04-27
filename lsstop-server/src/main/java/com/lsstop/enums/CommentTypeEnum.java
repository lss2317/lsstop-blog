package com.lsstop.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 评论目标类型枚举
 *
 * @author lishusheng
 * @date 2026/01/02
 */
@Getter
@AllArgsConstructor
public enum CommentTypeEnum {

    ARTICLE(1, "文章"),
    FRIEND_LINK(2, "友链"),
    TALK(3, "说说");

    /**
     * 类型码
     */
    private final Integer type;

    /**
     * 描述
     */
    private final String desc;

    /**
     * 根据类型码获取枚举
     *
     * @param type 类型码
     * @return 枚举值，不存在返回null
     */
    public static CommentTypeEnum of(Integer type) {
        if (type == null) {
            return null;
        }
        for (CommentTypeEnum value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }
}
