package com.lsstop.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 主页文章列表分页VO
 *
 * @author lishusheng
 * @date 2026/02/19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleHomePageVO {

    /**
     * 文章列表
     */
    private List<ArticleHomeVO> list;

    /**
     * 文章总数
     */
    private Integer total;
}
