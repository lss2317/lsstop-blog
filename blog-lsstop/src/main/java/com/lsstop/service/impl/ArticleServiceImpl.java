package com.lsstop.service.impl;

import com.lsstop.domain.vo.ArticleArchiveVO;
import com.lsstop.domain.vo.ArticleListVO;
import com.lsstop.mapper.ArticleMapper;
import com.lsstop.service.ArticleService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 文章服务实现类
 *
 * @author lishusheng
 * @date 2026/01/18
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    @Resource
    private ArticleMapper articleMapper;

    /**
     * 获取文章归档列表
     *
     * @return 文章归档列表
     */
    @Override
    public List<ArticleArchiveVO> getArchiveList() {
        return articleMapper.getArchiveList();
    }

    /**
     * 根据分类ID获取文章列表
     *
     * @param categoryId 分类ID
     * @return 文章列表
     */
    @Override
    public List<ArticleListVO> getArticleListByCategory(Integer categoryId) {
        return articleMapper.getArticleListByCategory(categoryId);
    }

    /**
     * 根据标签ID获取文章列表
     *
     * @param tagId 标签ID
     * @return 文章列表
     */
    @Override
    public List<ArticleListVO> getArticleListByTag(Integer tagId) {
        return articleMapper.getArticleListByTag(tagId);
    }

}
