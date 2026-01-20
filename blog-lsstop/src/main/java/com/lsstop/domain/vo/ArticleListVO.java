package com.lsstop.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类/标签页文章列表VO
 *
 * @author lishusheng
 * @date 2026/01/19
 */
@Data
public class ArticleListVO {

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
     * 发表时间
     */
    private LocalDateTime createTime;

}
