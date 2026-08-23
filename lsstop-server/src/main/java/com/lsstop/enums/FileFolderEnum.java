package com.lsstop.enums;

import com.lsstop.enums.base.EnumLookup;
import com.lsstop.enums.base.ValueEnum;
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
public enum FileFolderEnum implements ValueEnum<String> {

    AVATAR("avatar", "用户头像"),
    WEBSITE_CONFIG("website-config", "网站配置图片"),
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

    @Override
    public String getValue() {
        return folder;
    }

    /**
     * 根据文件夹名称获取枚举
     *
     * @param folder 文件夹名称
     * @return 枚举值，不存在返回null
     */
    public static FileFolderEnum of(String folder) {
        return EnumLookup.getOrNull(FileFolderEnum.class, folder);
    }

    /**
     * 判断文件夹名称是否有效
     *
     * @param folder 文件夹名称
     * @return 是否有效
     */
    public static boolean isValid(String folder) {
        return EnumLookup.contains(FileFolderEnum.class, folder);
    }
}
