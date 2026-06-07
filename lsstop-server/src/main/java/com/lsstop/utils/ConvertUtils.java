package com.lsstop.utils;

import com.lsstop.constant.CommonConst;

/**
 * 数据转换工具类
 *
 * @author lishusheng
 * @date 2026/06/07
 */
public class ConvertUtils {

    private ConvertUtils() {
    }

    /**
     * Boolean 转 Integer（null 按指定默认值处理）
     *
     * @param value      布尔值
     * @param defaultVal null 时的默认值
     * @return 启用=1，禁用=0
     */
    public static Integer boolToInt(Boolean value, boolean defaultVal) {
        return (value != null ? value : defaultVal) ? CommonConst.ENABLED : CommonConst.DISABLED;
    }

    /**
     * Boolean 转 Integer（null 视为 false）
     *
     * @param value 布尔值
     * @return 启用=1，禁用=0
     */
    public static Integer boolToInt(Boolean value) {
        return boolToInt(value, false);
    }

}
