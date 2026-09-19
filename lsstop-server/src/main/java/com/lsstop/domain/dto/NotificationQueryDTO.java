package com.lsstop.domain.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 通知列表查询参数。
 *
 * <p>控制层使用该对象接收 GET 查询参数，服务层完成参数校验后直接传给 Mapper；
 * 分页偏移量由当前页码和每页条数计算，避免通过无类型的 Map 传递查询条件。</p>
 *
 * @author lishusheng
 * @date 2026/09/19
 */
@Data
public class NotificationQueryDTO {

    /** 当前页码，从 1 开始 */
    private Integer current;

    /** 每页条数 */
    private Integer size;

    /** 通知分类：1异常告警 2安全告警 3系统通知 */
    private Integer category;

    /** 事件类型 */
    private String eventType;

    /** 通知级别：1提示 2警告 3错误 4严重 */
    private Integer level;

    /** 阅读状态：0未读 1已读 */
    private Integer readStatus;

    /** 来源模块 */
    private String sourceType;

    /** 标题或内容关键词 */
    private String keyword;

    /** 最近发生时间起点 */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime;

    /** 最近发生时间终点 */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endTime;

    /**
     * 获取数据库分页偏移量。
     *
     * @return 当前页对应的记录偏移量
     */
    public Integer getOffset() {
        return (current - 1) * size;
    }
}
