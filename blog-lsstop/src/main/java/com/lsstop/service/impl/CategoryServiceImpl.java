package com.lsstop.service.impl;

import com.lsstop.domain.vo.CategoryVO;
import com.lsstop.mapper.CategoryMapper;
import com.lsstop.service.CategoryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类服务实现类
 *
 * @author lishusheng
 * @date 2026/01/15
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    /**
     * 获取分类列表
     *
     * @return 分类列表
     */
    @Override
    public List<CategoryVO> getCategoryList() {
        return categoryMapper.getCategoryList();
    }
}
