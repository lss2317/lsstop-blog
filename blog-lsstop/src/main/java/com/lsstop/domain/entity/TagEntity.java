package com.lsstop.domain.entity;

import com.lsstop.domain.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 标签实体
 *
 * @author lishusheng
 * @date 2026/01/11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagEntity implements BaseData {

    /**
     * id
     */
    private Integer id;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 标签背景图URL
     */
    private String tagCover;

    /**
     * 是否删除 1是0否
     */
    private Integer isDelete;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
}
