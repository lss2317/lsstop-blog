package com.lsstop.domain.entity;

import com.lsstop.domain.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系统通知及异常告警实体
 *
 * @author lishusheng
 * @date 2026/09/06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEntity implements BaseData {

    /**
     * 通知主键ID
     */
    private Long id;

    /**
     * 通知唯一编号
     */
    private String notificationNo;

    /**
     * 通知分类：1异常告警 2安全告警 3系统通知
     */
    private Integer category;

    /**
     * 事件类型，如SYSTEM_EXCEPTION、SLOW_API、LOGIN_RISK
     */
    private String eventType;

    /**
     * 通知级别：1普通 2警告 3错误 4严重
     */
    private Integer level;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知摘要内容
     */
    private String content;

    /**
     * 来源类型，如HTTP_REQUEST、LOGIN_LOG、TASK、SYSTEM
     */
    private String sourceType;

    /**
     * 来源追踪编号，HTTP请求场景使用全局requestId
     */
    private String sourceId;

    /**
     * 经过脱敏的JSON扩展数据
     */
    private String extra;

    /**
     * 幂等及异常聚合键
     */
    private String dedupKey;

    /**
     * 事件累计发生次数
     */
    private Integer occurrenceCount;

    /**
     * 阅读时间，null表示未读
     */
    private LocalDateTime readTime;

    /**
     * 事件首次发生时间
     */
    private LocalDateTime firstOccurredTime;

    /**
     * 事件最近发生时间
     */
    private LocalDateTime lastOccurredTime;

    /**
     * 删除时间戳，0表示未删除
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
