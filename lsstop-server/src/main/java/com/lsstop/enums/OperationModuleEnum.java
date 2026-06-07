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

    ARTICLE("文章模块"),
    COMMENT("评论模块"),
    TAG("标签模块"),
    CATEGORY("分类模块"),
    USER("用户模块"),
    FRIEND_LINK("友链模块"),
    ANNOUNCEMENT("公告模块"),
    MESSAGE("留言模块"),
    TALK("说说模块"),
    PHOTO_ALBUM("相册模块"),
    PHOTO_ALBUM_IMAGE("相册图片模块"),
    PAGE_INFO("页面模块"),
    WEBSITE_CONFIG("网站配置模块"),
    FILE("文件模块"),
    CHAT_MESSAGE("聊天消息模块"),
    OPERATION_LOG("操作日志模块"),
    LOGIN_LOG("认证日志模块"),
    ROLE("角色模块"),
    MENU("菜单模块");

    /**
     * 描述
     */
    private final String desc;
}
