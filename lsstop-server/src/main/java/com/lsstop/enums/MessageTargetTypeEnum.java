package com.lsstop.enums;

import com.lsstop.enums.base.EnumLookup;
import com.lsstop.enums.base.ValueEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息目标对象类型枚举
 *
 * @author lishusheng
 * @date 2026/09/06
 */
@Getter
@AllArgsConstructor
public enum MessageTargetTypeEnum implements ValueEnum<String> {

    ARTICLE("ARTICLE", "文章"),
    COMMENT("COMMENT", "评论"),
    TALK("TALK", "说说"),
    USER("USER", "用户");

    /**
     * 目标类型
     */
    private final String type;

    /**
     * 目标描述
     */
    private final String desc;

    @Override
    public String getValue() {
        return type;
    }

    /**
     * 根据目标类型获取枚举
     *
     * @param type 目标类型
     * @return 对应枚举，不存在返回null
     */
    public static MessageTargetTypeEnum of(String type) {
        return EnumLookup.getOrNull(MessageTargetTypeEnum.class, type);
    }
}
