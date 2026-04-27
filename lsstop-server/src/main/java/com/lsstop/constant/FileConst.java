package com.lsstop.constant;

import java.util.Arrays;
import java.util.List;

/**
 * 文件常量
 *
 * @author lss
 * @date 2026/3/16
 */
public class FileConst {

    private FileConst() {
    }

    /**
     * 最大文件大小：5MB
     */
    public static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    /**
     * 最大文件大小描述
     */
    public static final String MAX_FILE_SIZE_DESC = "5MB";

    /**
     * 允许上传的图片 MIME 类型
     */
    public static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    /**
     * 允许上传的图片扩展名（小写，含点号）
     */
    public static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList(
            ".jpg",
            ".jpeg",
            ".png",
            ".gif",
            ".webp"
    );

    /**
     * 允许上传的图片格式描述
     */
    public static final String ALLOWED_IMAGE_TYPES_DESC = "JPG、PNG、GIF、WebP";

    /**
     * 文件为空错误
     */
    public static final String FILE_EMPTY = "文件不能为空";

    /**
     * 文件过大错误
     */
    public static final String FILE_TOO_LARGE = "文件大小不能超过" + MAX_FILE_SIZE_DESC;

    /**
     * 文件类型不支持错误
     */
    public static final String FILE_TYPE_NOT_SUPPORTED = "仅支持 " + ALLOWED_IMAGE_TYPES_DESC + " 格式的图片";

    /**
     * 文件上传失败错误
     */
    public static final String FILE_UPLOAD_FAILED = "文件上传失败";

    /**
     * 文件删除失败错误
     */
    public static final String FILE_DELETE_FAILED = "文件删除失败";
}
