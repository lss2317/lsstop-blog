package com.lsstop.enums;

import com.lsstop.enums.base.EnumLookup;
import com.lsstop.enums.base.ValueEnum;
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
public enum LikeTypeEnum implements ValueEnum<Integer> {

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

    @Override
    public Integer getValue() {
        return type;
    }

    /**
     * 根据类型码获取枚举
     *
     * @param type 类型码
     * @return 枚举值，不存在返回null
     */
    public static LikeTypeEnum of(Integer type) {
        return EnumLookup.getOrNull(LikeTypeEnum.class, type);
    }
}
