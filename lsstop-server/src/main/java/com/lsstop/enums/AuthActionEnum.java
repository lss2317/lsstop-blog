package com.lsstop.enums;

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
public enum AuthActionEnum {

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

    /**
     * 根据code获取枚举
     *
     * @param code 操作类型码
     * @return 操作类型枚举
     */
    public static AuthActionEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (AuthActionEnum action : values()) {
            if (action.getCode().equals(code)) {
                return action;
            }
        }
        return null;
    }

}
