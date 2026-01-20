package com.lsstop.mapper;

import com.lsstop.domain.vo.ArticleArchiveVO;
import com.lsstop.domain.vo.ArticleListVO;

import java.util.List;

/**
 * 文章数据访问层
 *
 * @author lishusheng
 * @date 2026/01/18
 */
public interface ArticleMapper {

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

}
