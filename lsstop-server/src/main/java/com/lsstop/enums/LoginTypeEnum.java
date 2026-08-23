package com.lsstop.enums;

import com.lsstop.enums.base.EnumLookup;
import com.lsstop.enums.base.ValueEnum;
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
public enum LoginTypeEnum implements ValueEnum<Integer> {

    /**
     * 邮箱登录
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

    @Override
    public Integer getValue() {
        return code;
    }

    /**
     * 根据code获取枚举
     *
     * @param code 登录方式码
     * @return 登录方式枚举
     */
    public static LoginTypeEnum getByCode(Integer code) {
        return EnumLookup.getOrNull(LoginTypeEnum.class, code);
    }

}
