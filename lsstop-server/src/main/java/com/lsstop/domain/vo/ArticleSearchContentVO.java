package com.lsstop.domain.vo;

import lombok.Data;

/**
 * 文章内容搜索结果VO
 *
 * @author lishusheng
 * @date 2026/03/08
 */
@Data
public class ArticleSearchContentVO {

    /**
     * 文章ID
     */
    private Integer id;

    /**
     * 文章标题
     */
    private String articleTitle;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 文章内容（包含搜索关键词的摘要片段）
     */
    private String articleContent;

}
