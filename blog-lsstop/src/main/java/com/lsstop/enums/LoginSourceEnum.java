package com.lsstop.enums;

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
public enum LoginSourceEnum {

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

    /**
     * 根据code获取枚举
     *
     * @param code 来源码
     * @return 登录来源枚举
     */
    public static LoginSourceEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (LoginSourceEnum source : values()) {
            if (source.getCode().equals(code)) {
                return source;
            }
        }
        return null;
    }

}
