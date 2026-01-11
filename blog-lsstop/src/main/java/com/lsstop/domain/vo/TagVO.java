package com.lsstop.domain.vo;

import lombok.Data;

/**
 * 标签VO
 *
 * @author lishusheng
 * @date 2026/01/11
 */
@Data
public class TagVO {

    /**
     * id
     */
    private Integer id;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 文章数量
     */
    private Integer articleCount;

}
