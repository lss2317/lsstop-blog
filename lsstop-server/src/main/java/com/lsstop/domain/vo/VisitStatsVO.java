package com.lsstop.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 访问统计VO
 *
 * @author lishusheng
 * @date 2026/02/19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitStatsVO {

    /**
     * 总访问量
     */
    private Integer viewsCount;

    /**
     * 今日独立访客数
     */
    private Integer todayUniqueVisitorCount;
}
