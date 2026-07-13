package com.lsstop.utils;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用户ID生成工具类
 * <p>
 * 生成16位固定长度的唯一用户标识（base62编码）
 * 结构：时间戳部分(8位) 与 随机+序列部分(8位) 交错混合
 * 单机场景下通过原子序列号保证同毫秒内不碰撞
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
     * 使用相对时间戳，减小数值范围
     */
    private static final long EPOCH = 1577836800000L;

    /**
     * 混淆因子，用于打乱时间戳规律
     */
    private static final long OBFUSCATE_FACTOR = 0x5DEECE66DL;

    /**
     * 同毫秒内的原子序列号，防止碰撞
     */
    private static final AtomicInteger SEQUENCE = new AtomicInteger(0);

    /**
     * 上一次生成时的时间戳
     */
    private static volatile long lastTimestamp = -1L;

    /**
     * 序列号占用的随机部分位数（前2位为序列，后6位为随机）
     * 单毫秒内最多支持 62^2 = 3844 次生成
     */
    private static final int SEQUENCE_LENGTH = 2;

    private UserUidUtils() {
    }

    /**
     * 生成16位唯一用户标识
     * 时间戳和随机+序列字符交错混合，无明显规律
     *
     * @return 16位唯一用户标识
     */
    public static String generate() {
        char[] uid = new char[UID_LENGTH];
        long timestamp = System.currentTimeMillis() - EPOCH;

        // 获取同毫秒内的序列号
        int seq = getSequence(timestamp);

        // 混淆时间戳，清除符号位确保非负
        long obfuscated = obfuscate(timestamp) & Long.MAX_VALUE;

        int partLength = UID_LENGTH / 2;

        // 生成时间戳部分（8位 base62）
        char[] timePart = new char[partLength];
        for (int i = partLength - 1; i >= 0; i--) {
            timePart[i] = ALPHABET[(int) (obfuscated % BASE)];
            obfuscated /= BASE;
        }

        // 生成随机+序列部分（前2位序列 + 后6位随机）
        char[] mixPart = new char[partLength];
        // 序列号编码到前2位
        int seqValue = seq;
        for (int i = SEQUENCE_LENGTH - 1; i >= 0; i--) {
            mixPart[i] = ALPHABET[seqValue % BASE];
            seqValue /= BASE;
        }
        // 剩余位填充随机字符
        for (int i = SEQUENCE_LENGTH; i < partLength; i++) {
            mixPart[i] = ALPHABET[ThreadLocalRandom.current().nextInt(BASE)];
        }

        // 交错混合：时间戳和随机+序列字符交替排列
        for (int i = 0; i < partLength; i++) {
            uid[i * 2] = timePart[i];
            uid[i * 2 + 1] = mixPart[i];
        }
        return new String(uid);
    }

    /**
     * 获取当前毫秒内的序列号
     * 同一毫秒内递增，不同毫秒重置为0
     */
    private static synchronized int getSequence(long timestamp) {
        if (timestamp == lastTimestamp) {
            return SEQUENCE.incrementAndGet();
        } else {
            lastTimestamp = timestamp;
            SEQUENCE.set(0);
            return 0;
        }
    }

    /**
     * 对数值进行位混淆，打乱规律性
     */
    private static long obfuscate(long value) {
        value ^= OBFUSCATE_FACTOR;
        value = (value ^ (value >>> 32)) * 0x45D9F3BL;
        return value ^ (value >>> 28);
    }
}
