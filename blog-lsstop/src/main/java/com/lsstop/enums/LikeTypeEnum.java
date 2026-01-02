package com.lsstop.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 点赞目标类型枚举
 *
 * @author lishusheng
 * @date 2026/01/02
 */
@Getter
@AllArgsConstructor
public enum LikeTypeEnum {

    TALK(1, "说说"),
    ARTICLE(2, "文章"),
    COMMENT(3, "评论");

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
    public static LikeTypeEnum of(Integer type) {
        if (type == null) {
            return null;
        }
        for (LikeTypeEnum value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }
}