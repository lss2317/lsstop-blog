package com.lsstop.enums;

import com.lsstop.enums.base.EnumLookup;
import com.lsstop.enums.base.ValueEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通知级别枚举
 *
 * @author lishusheng
 * @date 2026/09/06
 */
@Getter
@AllArgsConstructor
public enum NotificationLevelEnum implements ValueEnum<Integer> {

    NORMAL(1, "普通"),
    WARNING(2, "警告"),
    ERROR(3, "错误"),
    CRITICAL(4, "严重");

    /**
     * 级别码
     */
    private final Integer code;

    /**
     * 级别描述
     */
    private final String desc;

    @Override
    public Integer getValue() {
        return code;
    }

    /**
     * 根据级别码获取枚举
     *
     * @param code 级别码
     * @return 对应枚举，不存在返回null
     */
    public static NotificationLevelEnum of(Integer code) {
        return EnumLookup.getOrNull(NotificationLevelEnum.class, code);
    }
}
