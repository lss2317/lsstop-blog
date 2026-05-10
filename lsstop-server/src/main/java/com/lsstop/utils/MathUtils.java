package com.lsstop.utils;

/**
 * 数学计算工具类
 *
 * @author lishusheng
 * @date 2026/05/10
 */
public class MathUtils {

    /**
     * 计算环比变化百分比数值
     * <p>当上一周期为0时，若当前周期>0则返回100，否则返回0</p>
     *
     * @param current  当前周期值
     * @param previous 上一周期值
     * @return 环比变化百分比（如 20、-12、0）
     */
    public static int calcChangePercent(int current, int previous) {
        if (previous == 0) {
            return current > 0 ? 100 : 0;
        }
        return (int) Math.round((double) (current - previous) / previous * 100);
    }
}
