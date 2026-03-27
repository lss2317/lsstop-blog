package com.lsstop.utils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 用户ID生成工具类
 *
 * @author lishusheng
 * @date 2025/12/23
 */
public class UserUidUtils {

    private static final char[] ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final int BASE = ALPHABET.length;
    private static final int UID_LENGTH = 16;

    /**
     * 基准时间戳: 2020-01-01 00:00:00 UTC
     * 使用相对时间戳避免截断风险
     */
    private static final long EPOCH = 1577836800000L;

    /**
     * 混淆因子，用于打乱时间戳
     */
    private static final long OBFUSCATE_FACTOR = 0x5DEECE66DL;

    private UserUidUtils() {
    }

    /**
     * 生成16位唯一用户标识
     * 时间戳和随机字符交错混合，无明显规律
     *
     * @return 唯一用户标识
     */
    public static String generate() {
        char[] uid = new char[UID_LENGTH];
        // 使用相对时间戳，避免截断
        long timestamp = System.currentTimeMillis() - EPOCH;
        // XOR混淆时间戳，打乱规律
        timestamp = obfuscate(timestamp);

        int partLength = UID_LENGTH / 2;
        // 生成时间戳部分
        char[] timePart = new char[partLength];
        for (int i = partLength - 1; i >= 0; i--) {
            timePart[i] = ALPHABET[Math.floorMod(timestamp, BASE)];
            timestamp /= BASE;
        }
        // 生成随机部分
        char[] randomPart = new char[partLength];
        for (int i = 0; i < partLength; i++) {
            randomPart[i] = ALPHABET[ThreadLocalRandom.current().nextInt(BASE)];
        }
        // 交错混合：时间戳和随机字符交替排列
        for (int i = 0; i < partLength; i++) {
            uid[i * 2] = timePart[i];
            uid[i * 2 + 1] = randomPart[i];
        }
        return new String(uid);
    }

    /**
     * 对数值进行可逆的位混淆，打乱规律性
     */
    private static long obfuscate(long value) {
        value ^= OBFUSCATE_FACTOR;
        value = (value ^ (value >>> 32)) * 0x45D9F3BL;
        return value ^ (value >>> 28);
    }
}
