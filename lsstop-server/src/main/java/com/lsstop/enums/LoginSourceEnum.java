package com.lsstop.enums;

import com.lsstop.enums.base.EnumLookup;
import com.lsstop.enums.base.ValueEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录来源枚举
 *
 * @author lishusheng
 * @date 2026/01/17
 */
@Getter
@AllArgsConstructor
public enum LoginSourceEnum implements ValueEnum<Integer> {

    /**
     * 前台登录
     */
    FRONT(0, "前台"),

    /**
     * 后台登录
     */
    ADMIN(1, "后台"),

    /**
     * 非法登录
     */
    ILLEGAL(2, "非法");

    /**
     * 来源码
     */
    private final Integer code;

    /**
     * 来源描述
     */
    private final String desc;

    @Override
    public Integer getValue() {
        return code;
    }

    /**
     * 根据code获取枚举
     *
     * @param code 来源码
     * @return 登录来源枚举
     */
    public static LoginSourceEnum getByCode(Integer code) {
        return EnumLookup.getOrNull(LoginSourceEnum.class, code);
    }

}
