package com.lsstop.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 主页文章列表VO
 *
 * @author lishusheng
 * @date 2026/01/20
 */
@Data
public class ArticleHomeVO {

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
     * 文章摘要
     */
    private String articleAbstract;

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
     * 文章类型：1原创 2转载 3翻译
     */
    private Integer type;

    /**
     * 是否置顶：0否 1是
     */
    private Integer isTop;

    /**
     * 发表时间
     */
    private LocalDateTime createTime;

}
