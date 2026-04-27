package com.lsstop.service;

import com.lsstop.domain.vo.CategoryVO;

import java.util.List;

/**
 * 分类服务
 *
 * @author lishusheng
 * @date 2026/01/15
 */
public interface CategoryService {

    /**
     * 获取分类列表
     *
     * @return 分类列表
     */
    List<CategoryVO> getCategoryList();

}
