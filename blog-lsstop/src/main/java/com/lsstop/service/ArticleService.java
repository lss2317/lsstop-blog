package com.lsstop.service;

import com.lsstop.domain.vo.ArticleArchiveVO;
import com.lsstop.domain.vo.ArticleHomePageVO;
import com.lsstop.domain.vo.ArticleListVO;
import com.lsstop.domain.vo.ArticleSearchContentVO;
import com.lsstop.domain.vo.ArticleSearchTitleVO;
import com.lsstop.domain.vo.ArticleVO;

import java.util.List;

/**
 * 文章服务
 *
 * @author lishusheng
 * @date 2026/01/18
 */
public interface ArticleService {

    /**
     * 获取主页文章列表
     *
     * @param current 页码
     * @return 文章列表和总数
     */
    ArticleHomePageVO getHomeArticleList(Integer current);

    /**
     * 获取文章归档列表
     *
     * @return 文章归档列表
     */
    List<ArticleArchiveVO> getArchiveList();

    /**
     * 根据分类ID获取文章列表
     *
     * @param categoryId 分类ID
     * @return 文章列表
     */
    List<ArticleListVO> getArticleListByCategory(Integer categoryId);

    /**
     * 根据标签ID获取文章列表
     *
     * @param tagId 标签ID
     * @return 文章列表
     */
    List<ArticleListVO> getArticleListByTag(Integer tagId);

    /**
     * 根据ID获取文章详情
     *
     * @param id 文章ID
     * @param ip 客户端IP
     * @return 文章详情
     */
    ArticleVO getArticleById(Integer id, String ip);

    /**
     * 根据标题搜索文章
     *
     * @param keyword 搜索关键词
     * @return 文章搜索结果列表
     */
    List<ArticleSearchTitleVO> searchByTitle(String keyword);

    /**
     * 根据内容搜索文章
     *
     * @param keyword 搜索关键词
     * @return 文章搜索结果列表
     */
    List<ArticleSearchContentVO> searchByContent(String keyword);

}
