package com.lsstop.domain.entity;

import com.lsstop.domain.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文章实体
 *
 * @author lishusheng
 * @date 2026/01/14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleEntity implements BaseData {

    /**
     * id
     */
    private Integer id;

    /**
     * 文章作者ID
     */
    private String userId;

    /**
     * 文章分类ID
     */
    private Integer categoryId;

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
     * 文章正文内容
     */
    private String articleContent;

    /**
     * 文章字数
     */
    private Integer wordCount;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 文章类型：1原创 2转载 3翻译
     */
    private Integer type;

    /**
     * 原文链接（转载/翻译时填写）
     */
    private String originalUrl;

    /**
     * 是否置顶：0否 1是
     */
    private Integer isTop;

    /**
     * 文章状态：1公开 2私密
     */
    private Integer status;

    /**
     * 是否删除：0否 1是
     */
    private Integer isDelete;

    /**
     * 发表时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
