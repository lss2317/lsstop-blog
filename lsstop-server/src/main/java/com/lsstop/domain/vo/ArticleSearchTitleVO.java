package com.lsstop.domain.vo;

import lombok.Data;

/**
 * 文章标题搜索结果VO
 *
 * @author lishusheng
 * @date 2026/03/08
 */
@Data
public class ArticleSearchTitleVO {

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

}
