package com.lsstop.utils;

import java.security.SecureRandom;

/**
 * 验证码生成工具类
 *
 * @author lishusheng
 * @date 2025/12/23
 */
public class CodeGenerator {

    private static final char[] ALPHANUMERIC = "0123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz".toCharArray();
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 验证码长度
     */
    private static final int CODE_LENGTH = 6;

    private CodeGenerator() {
    }

    /**
     * 生成8位字母数字混合验证码
     * 排除易混淆字符: 0/O, 1/I/l
     *
     * @return 8位验证码
     */
    public static String generateVerifyCode() {
        char[] code = new char[CODE_LENGTH];
        for (int i = 0; i < CODE_LENGTH; i++) {
            code[i] = ALPHANUMERIC[RANDOM.nextInt(ALPHANUMERIC.length)];
        }
        return new String(code);
    }
}
