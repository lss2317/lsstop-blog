package com.lsstop.domain.vo;

import lombok.Data;

/**
 * 分类文章预览VO
 *
 * @author lishusheng
 */
@Data
public class CategoryArticleVO {

    /**
     * 文章ID
     */
    private Integer id;

    /**
     * 文章标题
     */
    private String articleTitle;
}
