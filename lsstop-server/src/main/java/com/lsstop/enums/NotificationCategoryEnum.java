package com.lsstop.enums;

import com.lsstop.enums.base.EnumLookup;
import com.lsstop.enums.base.ValueEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通知分类枚举
 *
 * @author lishusheng
 * @date 2026/09/06
 */
@Getter
@AllArgsConstructor
public enum NotificationCategoryEnum implements ValueEnum<Integer> {

    EXCEPTION_ALERT(1, "异常告警"),
    SECURITY_ALERT(2, "安全告警"),
    SYSTEM_NOTICE(3, "系统通知");

    /**
     * 分类码
     */
    private final Integer code;

    /**
     * 分类描述
     */
    private final String desc;

    @Override
    public Integer getValue() {
        return code;
    }

    /**
     * 根据分类码获取枚举
     *
     * @param code 分类码
     * @return 对应枚举，不存在返回null
     */
    public static NotificationCategoryEnum of(Integer code) {
        return EnumLookup.getOrNull(NotificationCategoryEnum.class, code);
    }
}
