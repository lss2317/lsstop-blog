package com.lsstop.enums.base;

/**
 * 可通过业务值查找的枚举。
 *
 * @param <V> 业务值类型
 * @author lishusheng
 * @date 2026/08/23
 */
public interface ValueEnum<V> {

    /**
     * 获取用于匹配枚举的业务值。
     *
     * @return 业务值
     */
    V getValue();
}
