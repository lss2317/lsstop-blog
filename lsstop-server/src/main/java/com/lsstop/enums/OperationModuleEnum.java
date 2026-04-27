package com.lsstop.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作模块枚举
 *
 * @author lishusheng
 * @date 2026/03/29
 */
@Getter
@AllArgsConstructor
public enum OperationModuleEnum {

    ARTICLE("文章"),
    COMMENT("评论"),
    TAG("标签"),
    CATEGORY("分类"),
    USER("用户"),
    FRIEND_LINK("友链"),
    ANNOUNCEMENT("公告"),
    MESSAGE("留言"),
    TALK("说说"),
    PHOTO_ALBUM("相册"),
    PHOTO_ALBUM_IMAGE("相册图片"),
    PAGE_INFO("页面"),
    WEBSITE_CONFIG("网站配置"),
    FILE("文件"),
    CHAT_MESSAGE("聊天消息");

    /**
     * 描述
     */
    private final String desc;
}
