package com.lsstop.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章归档VO
 *
 * @author lishusheng
 * @date 2026/01/18
 */
@Data
public class ArticleArchiveVO {

    /**
     * 文章ID
     */
    private Integer id;

    /**
     * 文章标题
     */
    private String articleTitle;

    /**
     * 发布时间
     */
    private LocalDateTime createTime;

}
