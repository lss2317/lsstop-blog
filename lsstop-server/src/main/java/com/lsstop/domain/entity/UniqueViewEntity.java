package com.lsstop.domain.entity;

import com.lsstop.domain.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 每日访问量统计实体
 *
 * @author lishusheng
 * @date 2026/02/19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UniqueViewEntity implements BaseData {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 统计日期
     */
    private LocalDate viewDate;

    /**
     * 当日访问量
     */
    private Integer viewsCount;

    /**
     * 当日独立访客数
     */
    private Integer uniqueVisitorCount;
}
