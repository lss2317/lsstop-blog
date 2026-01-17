package com.lsstop.enums;

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
public enum LoginResultEnum {

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

    /**
     * 根据code获取枚举
     *
     * @param code 结果码
     * @return 登录结果枚举
     */
    public static LoginResultEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (LoginResultEnum result : values()) {
            if (result.getCode().equals(code)) {
                return result;
            }
        }
        return null;
    }

}
