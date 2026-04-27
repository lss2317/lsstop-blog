package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.common.Result;
import com.lsstop.domain.vo.CategoryVO;
import com.lsstop.service.CategoryService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类控制层
 *
 * @author lishusheng
 * @date 2026/01/15
 */
@RestController
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    /**
     * 获取分类列表
     *
     * @return 分类列表
     */
    @GetMapping("/front/category/listCategory")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<List<CategoryVO>> getCategoryList() {
        return Result.success(categoryService.getCategoryList());
    }

}
