package com.lsstop.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 菜单类型枚举
 *
 * @author lishusheng
 * @date 2026/06/07
 */
@Getter
@AllArgsConstructor
public enum MenuTypeEnum {

    /**
     * 目录
     */
    DIRECTORY(1, "directory"),

    /**
     * 菜单
     */
    MENU(2, "menu"),

    /**
     * 按钮
     */
    BUTTON(3, "button"),

    /**
     * 内嵌
     */
    IFRAME(4, "iframe"),

    /**
     * 外链
     */
    LINK(5, "link");

    /**
     * 数据库存储值
     */
    private final Integer code;

    /**
     * 前端传值
     */
    private final String name;

    /**
     * 根据前端字符串获取数据库数值
     *
     * @param name 前端传值
     * @return 数据库数值，未匹配返回 null
     */
    public static Integer toCode(String name) {
        if (name == null) return null;
        for (MenuTypeEnum e : values()) {
            if (e.name.equals(name)) {
                return e.code;
            }
        }
        return null;
    }

}
