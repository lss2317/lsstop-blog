package com.lsstop.enums;

import com.lsstop.enums.base.EnumLookup;
import com.lsstop.enums.base.ValueEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息关联业务记录类型枚举
 *
 * @author lishusheng
 * @date 2026/09/06
 */
@Getter
@AllArgsConstructor
public enum MessageRelatedTypeEnum implements ValueEnum<String> {

    COMMENT("COMMENT", "评论记录"),
    LIKE_RECORD("LIKE_RECORD", "点赞记录"),
    FOLLOW_RECORD("FOLLOW_RECORD", "关注记录");

    /**
     * 关联类型
     */
    private final String type;

    /**
     * 关联描述
     */
    private final String desc;

    @Override
    public String getValue() {
        return type;
    }

    /**
     * 根据关联类型获取枚举
     *
     * @param type 关联类型
     * @return 对应枚举，不存在返回null
     */
    public static MessageRelatedTypeEnum of(String type) {
        return EnumLookup.getOrNull(MessageRelatedTypeEnum.class, type);
    }
}
