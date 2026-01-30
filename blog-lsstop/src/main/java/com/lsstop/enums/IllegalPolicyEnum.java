package com.lsstop.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 敏感词处理策略枚举
 *
 * @author lishusheng
 * @date 2026/01/30
 */
@Getter
@AllArgsConstructor
public enum IllegalPolicyEnum {

    BLOCK(0, "拦截"),
    REVIEW(1, "转审核"),
    REPLACE(2, "替换发布");

    /**
     * 策略码
     */
    private final Integer code;

    /**
     * 描述
     */
    private final String desc;

    /**
     * 根据策略码获取枚举
     *
     * @param code 策略码
     * @return 枚举值，不存在返回null
     */
    public static IllegalPolicyEnum of(Integer code) {
        if (code == null) {
            return null;
        }
        for (IllegalPolicyEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
