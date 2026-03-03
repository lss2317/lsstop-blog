package com.lsstop.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * 密码加密工具类
 * <p>
 * 使用PBKDF2WithHmacSHA256算法进行密码加密，
 * </p>
 *
 * @author lishusheng
 * @date 2026/01/03
 */
@Slf4j
public class PasswordUtils {

    /**
     * 加密算法
     */
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    /**
     * 盐值长度（字节）
     */
    private static final int SALT_LENGTH = 16;

    /**
     * 迭代次数（越高越安全，但越慢）
     */
    private static final int ITERATIONS = 65536;

    /**
     * 密钥长度（位）
     */
    private static final int KEY_LENGTH = 256;

    /**
     * 分隔符（用于分隔盐值和哈希值）
     */
    private static final String SEPARATOR = "$";

    /**
     * 分隔符正则（用于 split）
     */
    private static final String SEPARATOR_REGEX = "\\$";

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private PasswordUtils() {
        // 私有构造函数，防止实例化
    }

    /**
     * 加密密码
     * <p>
     * 返回格式：Base64(salt)$Base64(hash)
     * </p>
     *
     * @param password 明文密码
     * @return 加密后的密码字符串（包含盐值）
     */
    public static String encrypt(String password) {
        byte[] salt = generateSalt();
        byte[] hash = hash(password.toCharArray(), salt);

        String saltBase64 = Base64.getEncoder().encodeToString(salt);
        String hashBase64 = Base64.getEncoder().encodeToString(hash);

        return saltBase64 + SEPARATOR + hashBase64;
    }

    /**
     * 验证密码
     *
     * @param password        明文密码
     * @param encryptedPassword 加密后的密码
     * @return true-密码匹配，false-密码不匹配
     */
    public static boolean verify(String password, String encryptedPassword) {
        if (password == null || encryptedPassword == null) {
            return false;
        }

        String[] parts = encryptedPassword.split(SEPARATOR_REGEX, 2);
        if (parts.length != 2) {
            log.debug("加密密码格式无效");
            return false;
        }

        try {
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[1]);
            byte[] actualHash = hash(password.toCharArray(), salt);

            return slowEquals(expectedHash, actualHash);
        } catch (IllegalArgumentException e) {
            log.debug("解码加密密码失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 生成随机盐值
     *
     * @return 盐值字节数组
     */
    private static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        SECURE_RANDOM.nextBytes(salt);
        return salt;
    }

    /**
     * 使用PBKDF2算法哈希密码
     *
     * @param password 密码字符数组
     * @param salt     盐值
     * @return 哈希值
     */
    private static byte[] hash(char[] password, byte[] salt) {
        PBEKeySpec spec = null;
        try {
            spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            return factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("密码哈希失败: {}", e.getMessage());
            throw new RuntimeException("密码加密失败", e);
        } finally {
            if (spec != null) {
                spec.clearPassword();
            }
        }
    }

    /**
     * 时间恒定的字节数组比较（防止时序攻击）
     *
     * @param a 字节数组a
     * @param b 字节数组b
     * @return true-相等，false-不相等
     */
    private static boolean slowEquals(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }
        int diff = 0;
        for (int i = 0; i < a.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }
}
