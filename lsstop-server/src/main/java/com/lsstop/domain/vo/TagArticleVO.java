package com.lsstop.domain.vo;

import lombok.Data;

/**
 * 标签文章预览VO
 *
 * @author lishusheng
 */
@Data
public class TagArticleVO {

    /**
     * 文章ID
     */
    private Integer id;

    /**
     * 文章标题
     */
    private String articleTitle;
}
