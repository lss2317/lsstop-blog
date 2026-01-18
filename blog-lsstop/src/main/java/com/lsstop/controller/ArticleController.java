package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.common.Result;
import com.lsstop.domain.vo.ArticleArchiveVO;
import com.lsstop.service.ArticleService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 文章控制层
 *
 * @author lishusheng
 * @date 2026/01/18
 */
@RestController
public class ArticleController {

    @Resource
    private ArticleService articleService;

    /**
     * 获取文章归档列表
     *
     * @return 文章归档列表
     */
    @GetMapping("/front/article/archives")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<List<ArticleArchiveVO>> getArchiveList() {
        return Result.success(articleService.getArchiveList());
    }

}
