package com.lsstop.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件夹类型枚举
 *
 * @author lss
 * @date 2026/3/16
 */
@Getter
@AllArgsConstructor
public enum FileFolderEnum {

    AVATAR("avatar", "用户头像"),
    WEBSITE_AVATAR("website-avatar", "网站配置头像"),
    ARTICLE("article", "文章图片"),
    ALBUM("album", "相册"),
    TALK("talk", "说说"),
    CHAT("chat", "聊天室");

    /**
     * 文件夹名称
     */
    private final String folder;

    /**
     * 描述
     */
    private final String desc;

    /**
     * 根据文件夹名称获取枚举
     *
     * @param folder 文件夹名称
     * @return 枚举值，不存在返回null
     */
    public static FileFolderEnum of(String folder) {
        if (folder == null || folder.isEmpty()) {
            return null;
        }
        for (FileFolderEnum value : values()) {
            if (value.folder.equals(folder)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 判断文件夹名称是否有效
     *
     * @param folder 文件夹名称
     * @return 是否有效
     */
    public static boolean isValid(String folder) {
        return of(folder) != null;
    }
}
