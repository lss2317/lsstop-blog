package com.lsstop.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 验证码用途枚举
 *
 * @author lishusheng
 * @date 2026/02/21
 */
@Getter
@AllArgsConstructor
public enum CodePurposeEnum {

    /**
     * 邮箱验证码登录
     */
    LOGIN(1, "login", "登录", "登录"),

    /**
     * 用户注册
     */
    REGISTER(2, "register", "注册", "注册"),

    /**
     * 重置密码
     */
    RESET_PASSWORD(3, "reset_password", "重置密码", "重置密码"),

    /**
     * 修改邮箱
     */
    CHANGE_EMAIL(4, "change_email", "修改邮箱", "修改邮箱");

    /**
     * 用途编码
     */
    private final Integer code;

    /**
     * 用途标识（用于Redis key）
     */
    private final String key;

    /**
     * 用途描述
     */
    private final String desc;

    /**
     * 邮件场景标题
     */
    private final String sceneTitle;

    /**
     * 根据编码获取枚举
     *
     * @param code 编码
     * @return 枚举值，不存在返回null
     */
    public static CodePurposeEnum of(Integer code) {
        if (code == null) {
            return null;
        }
        for (CodePurposeEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
