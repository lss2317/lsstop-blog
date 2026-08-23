package com.lsstop.enums;

import com.lsstop.enums.base.EnumLookup;
import com.lsstop.enums.base.ValueEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录结果枚举
 *
 * @author lishusheng
 * @date 2026/01/17
 */
@Getter
@AllArgsConstructor
public enum LoginResultEnum implements ValueEnum<Integer> {

    /**
     * 登录成功
     */
    SUCCESS(0, "成功"),

    /**
     * 登录失败
     */
    FAIL(1, "失败");

    /**
     * 结果码
     */
    private final Integer code;

    /**
     * 结果描述
     */
    private final String desc;

    @Override
    public Integer getValue() {
        return code;
    }

    /**
     * 根据code获取枚举
     *
     * @param code 结果码
     * @return 登录结果枚举
     */
    public static LoginResultEnum getByCode(Integer code) {
        return EnumLookup.getOrNull(LoginResultEnum.class, code);
    }

}
