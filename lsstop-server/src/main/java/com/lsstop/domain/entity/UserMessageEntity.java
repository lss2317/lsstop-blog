package com.lsstop.domain.entity;

import com.lsstop.domain.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户互动消息实体
 *
 * @author lishusheng
 * @date 2026/09/06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMessageEntity implements BaseData {

    /**
     * 消息主键ID
     */
    private Long id;

    /**
     * 消息唯一编号
     */
    private String messageNo;

    /**
     * 消息触发用户UID
     */
    private String senderId;

    /**
     * 消息接收用户UID
     */
    private String recipientId;

    /**
     * 事件类型，如COMMENT_REPLY、COMMENT_LIKE、ARTICLE_LIKE
     */
    private String eventType;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容摘要
     */
    private String content;

    /**
     * 被操作的目标对象类型，如ARTICLE、COMMENT、TALK、USER
     */
    private String targetType;

    /**
     * 被操作的目标对象ID，如被点赞的文章ID、被回复的原评论ID
     */
    private String targetId;

    /**
     * 本次操作产生的业务记录类型，如COMMENT、LIKE_RECORD、FOLLOW_RECORD
     */
    private String relatedType;

    /**
     * 本次操作产生的业务记录ID，如新回复ID、点赞记录ID、关注记录ID
     */
    private String relatedId;

    /**
     * 点击消息后的前台跳转路径
     */
    private String actionPath;

    /**
     * 消息幂等键，同一业务事件必须使用相同值，用于防止重复创建消息
     */
    private String dedupKey;

    /**
     * 阅读时间，null表示未读
     */
    private LocalDateTime readTime;

    /**
     * 接收用户删除时间戳，0表示未删除
     */
    private Long deletedAt;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
