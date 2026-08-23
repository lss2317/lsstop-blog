package com.lsstop.enums;

import com.lsstop.enums.base.EnumLookup;
import com.lsstop.enums.base.ValueEnum;
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
public enum IllegalPolicyEnum implements ValueEnum<Integer> {

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

    @Override
    public Integer getValue() {
        return code;
    }

    /**
     * 根据策略码获取枚举
     *
     * @param code 策略码
     * @return 枚举值，不存在返回null
     */
    public static IllegalPolicyEnum of(Integer code) {
        return EnumLookup.getOrNull(IllegalPolicyEnum.class, code);
    }
}
