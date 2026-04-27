package com.lsstop.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告VO
 *
 * @author lishusheng
 * @date 2026/02/20
 */
@Data
public class AnnouncementVO {

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
     * 创建时间
     */
    private LocalDateTime createTime;

}
