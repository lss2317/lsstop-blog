package com.lsstop.enums.base;

import java.util.Objects;

/**
 * 枚举值查找工具。
 *
 * @author lishusheng
 * @date 2026/08/23
 */
public final class EnumLookup {

    private EnumLookup() {
    }

    /**
     * 根据业务值获取枚举。
     *
     * @param enumType 枚举类型
     * @param value    业务值
     * @param <V>      业务值类型
     * @param <E>      枚举类型
     * @return 匹配的枚举，不存在或业务值为 null 时返回 null
     */
    public static <V, E extends Enum<E> & ValueEnum<V>> E getOrNull(Class<E> enumType, V value) {
        Objects.requireNonNull(enumType, "enumType must not be null");
        if (value == null) {
            return null;
        }
        for (E enumValue : enumType.getEnumConstants()) {
            if (Objects.equals(enumValue.getValue(), value)) {
                return enumValue;
            }
        }
        return null;
    }

    /**
     * 判断业务值是否对应有效枚举。
     *
     * @param enumType 枚举类型
     * @param value    业务值
     * @param <V>      业务值类型
     * @param <E>      枚举类型
     * @return 是否存在匹配的枚举
     */
    public static <V, E extends Enum<E> & ValueEnum<V>> boolean contains(Class<E> enumType, V value) {
        return getOrNull(enumType, value) != null;
    }
}
