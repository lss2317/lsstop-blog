package com.lsstop.utils;

import java.util.UUID;

/**
 * 文件工具类
 *
 * @author lss
 * @date 2026/3/16
 */
public class FileUtils {

    private FileUtils() {
    }

    /**
     * 生成唯一文件名（UUID + 原扩展名）
     *
     * @param originalFilename 原始文件名
     * @return 唯一文件名
     */
    public static String generateUniqueFileName(String originalFilename) {
        String extension = getExtension(originalFilename);
        return UUID.randomUUID().toString().replace("-", "") + extension;
    }

    /**
     * 获取文件扩展名（包含点号）
     *
     * @param filename 文件名
     * @return 扩展名（如 ".jpg"），无扩展名返回空字符串
     */
    public static String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    /**
     * 获取文件扩展名（不含点号）
     *
     * @param filename 文件名
     * @return 扩展名（如 "jpg"），无扩展名返回空字符串
     */
    public static String getExtensionWithoutDot(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    /**
     * 获取不带扩展名的文件名
     *
     * @param filename 文件名
     * @return 不带扩展名的文件名
     */
    public static String getNameWithoutExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int dotIndex = filename.lastIndexOf(".");
        return dotIndex > 0 ? filename.substring(0, dotIndex) : filename;
    }
}
