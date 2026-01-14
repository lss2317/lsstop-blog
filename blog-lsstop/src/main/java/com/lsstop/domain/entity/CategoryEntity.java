package com.lsstop.domain.entity;

import com.lsstop.domain.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 分类实体
 *
 * @author lishusheng
 * @date 2026/01/14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryEntity implements BaseData {

    /**
     * id
     */
    private Integer id;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 分类背景图URL
     */
    private String categoryCover;

    /**
     * 是否删除：0否 1是
     */
    private Integer isDelete;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
