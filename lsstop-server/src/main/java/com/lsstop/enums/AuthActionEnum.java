package com.lsstop.enums;

import com.lsstop.enums.base.EnumLookup;
import com.lsstop.enums.base.ValueEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 认证操作类型枚举
 *
 * @author lishusheng
 * @date 2026/05/28
 */
@Getter
@AllArgsConstructor
public enum AuthActionEnum implements ValueEnum<Integer> {

    /**
     * 登录
     */
    LOGIN(1, "登录"),

    /**
     * 退出
     */
    LOGOUT(2, "退出"),

    /**
     * 注册
     */
    REGISTER(3, "注册");

    /**
     * 操作类型码
     */
    private final Integer code;

    /**
     * 操作类型描述
     */
    private final String desc;

    @Override
    public Integer getValue() {
        return code;
    }

    /**
     * 根据code获取枚举
     *
     * @param code 操作类型码
     * @return 操作类型枚举
     */
    public static AuthActionEnum getByCode(Integer code) {
        return EnumLookup.getOrNull(AuthActionEnum.class, code);
    }

}
