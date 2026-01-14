package com.lsstop.domain.entity;

import com.lsstop.domain.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章标签关联实体
 *
 * @author lishusheng
 * @date 2026/01/14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleTagEntity implements BaseData {

    /**
     * id
     */
    private Integer id;

    /**
     * 文章ID
     */
    private Integer articleId;

    /**
     * 标签ID
     */
    private Integer tagId;
}
