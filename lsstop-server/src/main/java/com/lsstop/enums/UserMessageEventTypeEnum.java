package com.lsstop.enums;

import com.lsstop.enums.base.EnumLookup;
import com.lsstop.enums.base.ValueEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户互动消息事件类型枚举
 *
 * @author lishusheng
 * @date 2026/09/06
 */
@Getter
@AllArgsConstructor
public enum UserMessageEventTypeEnum implements ValueEnum<String> {

    ARTICLE_COMMENT("ARTICLE_COMMENT", "文章评论"),
    TALK_COMMENT("TALK_COMMENT", "说说评论"),
    COMMENT_REPLY("COMMENT_REPLY", "评论回复"),
    ARTICLE_LIKE("ARTICLE_LIKE", "文章点赞"),
    TALK_LIKE("TALK_LIKE", "说说点赞"),
    COMMENT_LIKE("COMMENT_LIKE", "评论点赞"),
    USER_FOLLOW("USER_FOLLOW", "用户关注");

    /**
     * 事件类型
     */
    private final String type;

    /**
     * 事件描述
     */
    private final String desc;

    @Override
    public String getValue() {
        return type;
    }

    /**
     * 根据事件类型获取枚举
     *
     * @param type 事件类型
     * @return 对应枚举，不存在返回null
     */
    public static UserMessageEventTypeEnum of(String type) {
        return EnumLookup.getOrNull(UserMessageEventTypeEnum.class, type);
    }
}
