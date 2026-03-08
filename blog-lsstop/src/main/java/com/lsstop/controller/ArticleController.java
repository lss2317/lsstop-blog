package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.common.Result;
import com.lsstop.constant.CommentConst;
import com.lsstop.domain.vo.ArticleArchiveVO;
import com.lsstop.domain.vo.ArticleHomePageVO;
import com.lsstop.domain.vo.ArticleListVO;
import com.lsstop.domain.vo.ArticleSearchContentVO;
import com.lsstop.domain.vo.ArticleSearchTitleVO;
import com.lsstop.domain.vo.ArticleVO;
import com.lsstop.enums.StatusEnum;
import com.lsstop.exception.BusinessException;
import com.lsstop.service.ArticleService;
import com.lsstop.utils.IpUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
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
     * 获取主页文章列表
     *
     * @param current 页码
     * @return 文章列表和总数
     */
    @GetMapping("/front/article/listArticleHome")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<ArticleHomePageVO> getHomeArticleList(@RequestParam Integer current) {
        if (current < 1) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), CommentConst.INVALID_PAGE_PARAM);
        }
        return Result.success(articleService.getHomeArticleList(current));
    }

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

    /**
     * 根据分类ID获取文章列表
     *
     * @param categoryId 分类ID
     * @return 文章列表
     */
    @GetMapping("/front/article/category/{categoryId}")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<List<ArticleListVO>> getArticleListByCategory(@PathVariable Integer categoryId) {
        return Result.success(articleService.getArticleListByCategory(categoryId));
    }

    /**
     * 根据标签ID获取文章列表
     *
     * @param tagId 标签ID
     * @return 文章列表
     */
    @GetMapping("/front/article/tag/{tagId}")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<List<ArticleListVO>> getArticleListByTag(@PathVariable Integer tagId) {
        return Result.success(articleService.getArticleListByTag(tagId));
    }

    /**
     * 根据ID获取文章详情
     *
     * @param id 文章ID
     * @return 文章详情
     */
    @GetMapping("/front/article/{id}")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<ArticleVO> getArticleById(@PathVariable Integer id, HttpServletRequest request) {
        String ip = IpUtils.getIpAddress(request);
        return Result.success(articleService.getArticleById(id, ip));
    }

    /**
     * 根据标题搜索文章
     *
     * @param keyword 搜索关键词
     * @return 文章搜索结果列表
     */
    @GetMapping("/front/article/search/title")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<List<ArticleSearchTitleVO>> searchByTitle(@RequestParam String keyword) {
        return Result.success(articleService.searchByTitle(keyword));
    }

    /**
     * 根据内容搜索文章
     *
     * @param keyword 搜索关键词
     * @return 文章搜索结果列表
     */
    @GetMapping("/front/article/search/content")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<List<ArticleSearchContentVO>> searchByContent(@RequestParam String keyword) {
        return Result.success(articleService.searchByContent(keyword));
    }

}
