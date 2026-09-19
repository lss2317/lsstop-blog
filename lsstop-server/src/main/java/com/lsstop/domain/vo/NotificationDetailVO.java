package com.lsstop.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知详情。
 *
 * @author lishusheng
 * @date 2026/09/19
 */
@Data
public class NotificationDetailVO {

    /** 通知唯一编号 */
    private String notificationNo;

    /** 通知分类：1异常告警 2安全告警 3系统通知 */
    private Integer category;

    /** 事件类型 */
    private String eventType;

    /** 通知级别：1提示 2警告 3错误 4严重 */
    private Integer level;

    /** 通知标题 */
    private String title;

    /** 通知摘要 */
    private String content;

    /** 来源模块 */
    private String sourceType;

    /** 关联追踪编号 */
    private String sourceId;

    /** 已脱敏的 JSON 扩展数据 */
    private String extra;

    /** 聚合窗口内累计发生次数 */
    private Integer occurrenceCount;

    /** 阅读时间，为空表示未读 */
    private LocalDateTime readTime;

    /** 首次发生时间 */
    private LocalDateTime firstOccurredTime;

    /** 最近发生时间 */
    private LocalDateTime lastOccurredTime;

    /** 创建时间 */
    private LocalDateTime createTime;
}
