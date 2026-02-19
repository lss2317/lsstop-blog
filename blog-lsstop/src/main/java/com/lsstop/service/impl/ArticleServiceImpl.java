package com.lsstop.service.impl;

import com.lsstop.constant.ArticleConst;
import com.lsstop.constant.CommentConst;
import com.lsstop.constant.RedisConst;
import com.lsstop.domain.vo.ArticleArchiveVO;
import com.lsstop.domain.vo.ArticleHomePageVO;
import com.lsstop.domain.vo.ArticleHomeVO;
import com.lsstop.domain.vo.ArticleListVO;
import com.lsstop.domain.vo.ArticleSimpleVO;
import com.lsstop.domain.vo.ArticleVO;
import com.lsstop.exception.BusinessException;
import com.lsstop.mapper.ArticleMapper;
import com.lsstop.service.ArticleService;
import com.lsstop.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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

    @Resource
    private RedisUtils redisUtils;

    /**
     * 获取主页文章列表
     *
     * @param current 页码
     * @return 文章列表和总数
     */
    @Override
    public ArticleHomePageVO getHomeArticleList(Integer current) {
        int offset = (current - 1) * CommentConst.DEFAULT_PAGE_SIZE;
        List<ArticleHomeVO> list = articleMapper.getHomeArticleList(offset, CommentConst.DEFAULT_PAGE_SIZE);
        Integer total = articleMapper.getArticleCount();
        return new ArticleHomePageVO(list, total);
    }

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

    /**
     * 根据ID获取文章详情
     *
     * @param id 文章ID
     * @return 文章详情
     */
    @Override
    public ArticleVO getArticleById(Integer id, String ip) {
        ArticleVO articleVO = articleMapper.getArticleById(id);
        if (articleVO == null) {
            throw new BusinessException(ArticleConst.ARTICLE_NOT_FOUND);
        }
        // 浏览量防刷：同一IP 1小时内不重复计数
        String viewRecordKey = RedisConst.ARTICLE_VIEW_RECORD + id + ":" + ip;
        if (!redisUtils.hasKey(viewRecordKey)) {
            redisUtils.increment(RedisConst.ARTICLE_VIEW_COUNT + id);
            redisUtils.set(viewRecordKey, 1, ArticleConst.VIEW_RECORD_EXPIRE);
        }
        // 获取浏览量
        Integer viewCount = redisUtils.get(RedisConst.ARTICLE_VIEW_COUNT + id, Integer.class);
        articleVO.setViewCount(viewCount == null ? 0 : viewCount);
        // 从Redis获取点赞数
        Integer likeCount = redisUtils.get(RedisConst.ARTICLE_LIKE_COUNT + id, Integer.class);
        articleVO.setLikeCount(likeCount == null ? 0 : likeCount);

        // 并行查询上/下篇、最新、推荐文章
        CompletableFuture<ArticleSimpleVO> preFuture = CompletableFuture.supplyAsync(() ->
                articleMapper.getPreArticle(articleVO.getCreateTime()));
        CompletableFuture<ArticleSimpleVO> nextFuture = CompletableFuture.supplyAsync(() ->
                articleMapper.getNextArticle(articleVO.getCreateTime()));
        CompletableFuture<List<ArticleSimpleVO>> newestFuture = CompletableFuture.supplyAsync(() ->
                getNewestArticles(id));
        CompletableFuture<List<ArticleSimpleVO>> recommendFuture = CompletableFuture.supplyAsync(() ->
                getRecommendArticles(articleVO.getCategoryId(), id));

        // 等待所有查询完成并设置结果
        CompletableFuture.allOf(preFuture, nextFuture, newestFuture, recommendFuture).join();
        articleVO.setPreArticle(preFuture.join());
        articleVO.setNextArticle(nextFuture.join());
        articleVO.setNewestArticles(newestFuture.join());
        articleVO.setRecommendArticles(recommendFuture.join());

        return articleVO;
    }

    /**
     * 获取最新文章（优先从缓存获取，排除当前文章）
     *
     * @param excludeId 排除的文章ID
     * @return 最新文章列表
     */
    private List<ArticleSimpleVO> getNewestArticles(Integer excludeId) {
        List<ArticleSimpleVO> newestArticles = redisUtils.getList(RedisConst.NEWEST_ARTICLES, ArticleSimpleVO.class);
        if (newestArticles == null || newestArticles.isEmpty()) {
            // 多取1篇，确保过滤后仍有足够数量
            newestArticles = articleMapper.getNewestArticles(ArticleConst.NEWEST_ARTICLE_LIMIT + 1);
            if (newestArticles != null && !newestArticles.isEmpty()) {
                redisUtils.set(RedisConst.NEWEST_ARTICLES, newestArticles, ArticleConst.NEWEST_ARTICLE_CACHE_EXPIRE);
            }
        }
        if (newestArticles != null && excludeId != null) {
            newestArticles = newestArticles.stream()
                    .filter(article -> !excludeId.equals(article.getId()))
                    .limit(ArticleConst.NEWEST_ARTICLE_LIMIT)
                    .toList();
        }
        return newestArticles;
    }

    /**
     * 获取推荐文章（同分类下的文章，不足时补热门文章）
     *
     * @param categoryId 分类ID
     * @param excludeId  排除的文章ID
     * @return 推荐文章列表
     */
    private List<ArticleSimpleVO> getRecommendArticles(Integer categoryId, Integer excludeId) {
        List<ArticleSimpleVO> recommendArticles = articleMapper.getRecommendArticles(categoryId, excludeId, ArticleConst.RECOMMEND_ARTICLE_LIMIT);
        if (recommendArticles == null) {
            recommendArticles = new ArrayList<>();
        }
        if (recommendArticles.size() < ArticleConst.RECOMMEND_ARTICLE_LIMIT) {
            List<Integer> excludeIds = new ArrayList<>();
            excludeIds.add(excludeId);
            excludeIds.addAll(recommendArticles.stream().map(ArticleSimpleVO::getId).toList());
            List<ArticleSimpleVO> hotArticles = articleMapper.getHotArticles(excludeIds, ArticleConst.RECOMMEND_ARTICLE_LIMIT - recommendArticles.size());
            if (hotArticles != null) {
                recommendArticles.addAll(hotArticles);
            }
        }
        return recommendArticles;
    }

}
