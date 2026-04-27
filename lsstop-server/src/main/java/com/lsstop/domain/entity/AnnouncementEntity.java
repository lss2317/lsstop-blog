package com.lsstop.domain.entity;

import com.lsstop.domain.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 公告实体
 *
 * @author lishusheng
 * @date 2026/01/14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementEntity implements BaseData {

    /**
     * 公告ID
     */
    private Integer id;

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 类型：1-弹窗公告 2-首页展示 3-两者都有
     */
    private Integer type;

    /**
     * 显示优先级，值越大越靠前
     */
    private Integer priority;

    /**
     * 是否启用：0-禁用 1-启用
     */
    private Integer isEnabled;

    /**
     * 生效开始时间
     */
    private LocalDateTime startTime;

    /**
     * 生效结束时间
     */
    private LocalDateTime endTime;

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
