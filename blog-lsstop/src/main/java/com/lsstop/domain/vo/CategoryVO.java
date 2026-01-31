package com.lsstop.domain.vo;

import lombok.Data;

/**
 * 分类VO
 *
 * @author lishusheng
 * @date 2026/01/15
 */
@Data
public class CategoryVO {

    /**
     * id
     */
    private Integer id;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 文章数量
     */
    private Integer articleCount;

}
