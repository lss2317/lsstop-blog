package com.lsstop.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 简化文章VO（用于上一篇/下一篇/最新/推荐文章）
 *
 * @author lishusheng
 * @date 2026/01/22
 */
@Data
public class ArticleSimpleVO {

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
     * 创建时间
     */
    private LocalDateTime createTime;

}
