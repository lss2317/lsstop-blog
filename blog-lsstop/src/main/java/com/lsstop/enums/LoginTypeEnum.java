package com.lsstop.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录方式枚举
 *
 * @author lishusheng
 * @date 2026/01/03
 */
@Getter
@AllArgsConstructor
public enum LoginTypeEnum {

    /**
     * 邮箱密码登录
     */
    EMAIL(1, "邮箱"),

    /**
     * QQ登录
     */
    QQ(2, "QQ"),

    /**
     * 微博登录
     */
    WEIBO(3, "微博");

    /**
     * 登录方式码
     */
    private final Integer code;

    /**
     * 登录方式描述
     */
    private final String desc;

    /**
     * 根据code获取枚举
     *
     * @param code 登录方式码
     * @return 登录方式枚举
     */
    public static LoginTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (LoginTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

}
