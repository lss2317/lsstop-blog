package com.lsstop.mapper;

import com.lsstop.domain.entity.UniqueViewEntity;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

/**
 * 每日访问量统计数据访问层
 *
 * @author lishusheng
 * @date 2026/02/19
 */
public interface UniqueViewMapper {

    /**
     * 查询总访问量
     *
     * @return 总访问量
     */
    Integer getTotalViewsCount();

    /**
     * 根据日期查询访问量记录
     *
     * @param viewDate 统计日期
     * @return 访问量实体
     */
    UniqueViewEntity getByViewDate(@Param("viewDate") LocalDate viewDate);

    /**
     * 新增访问量记录
     *
     * @param entity 访问量实体
     * @return 影响行数
     */
    int insert(UniqueViewEntity entity);

    /**
     * 更新访问量
     *
     * @param viewDate   统计日期
     * @param viewsCount 访问量
     * @return 影响行数
     */
    int updateViewsCount(@Param("viewDate") LocalDate viewDate, @Param("viewsCount") Integer viewsCount);
}
