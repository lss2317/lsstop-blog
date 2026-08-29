package com.lsstop.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 后台公告VO
 *
 * @author lishusheng
 * @date 2026/08/29
 */
@Data
public class AnnouncementAdminVO {

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
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
