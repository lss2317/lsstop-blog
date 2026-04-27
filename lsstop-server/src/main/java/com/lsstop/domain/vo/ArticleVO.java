package com.lsstop.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章VO
 *
 * @author lishusheng
 * @date 2026/01/22
 */
@Data
public class ArticleVO {

    /**
     * 文章ID
     */
    private Integer id;

    /**
     * 文章封面图URL
     */
    private String articleCover;

    /**
     * 文章标题
     */
    private String articleTitle;

    /**
     * 文章内容
     */
    private String articleContent;

    /**
     * 分类ID
     */
    private Integer categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 标签列表
     */
    private List<TagSimpleVO> tags;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 上一篇文章
     */
    private ArticleSimpleVO preArticle;

    /**
     * 下一篇文章
     */
    private ArticleSimpleVO nextArticle;

    /**
     * 最新文章列表
     */
    private List<ArticleSimpleVO> newestArticles;

    /**
     * 推荐文章列表
     */
    private List<ArticleSimpleVO> recommendArticles;

}
