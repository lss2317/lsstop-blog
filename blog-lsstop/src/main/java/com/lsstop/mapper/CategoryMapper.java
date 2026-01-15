package com.lsstop.mapper;

import com.lsstop.domain.vo.CategoryVO;

import java.util.List;

/**
 * 分类数据访问层
 *
 * @author lishusheng
 * @date 2026/01/15
 */
public interface CategoryMapper {

    /**
     * 获取分类列表
     *
     * @return 分类列表
     */
    List<CategoryVO> getCategoryList();

}
