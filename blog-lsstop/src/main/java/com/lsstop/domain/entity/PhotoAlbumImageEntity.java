package com.lsstop.domain.entity;

import com.lsstop.domain.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 照片详情实体
 *
 * @author lishusheng
 * @date 2026/01/08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotoAlbumImageEntity implements BaseData {

    /**
     * id
     */
    private Integer id;

    /**
     * 相册id
     */
    private Integer albumId;

    /**
     * 照片地址
     */
    private String photoSrc;

    /**
     * 排序值，值越大越靠前
     */
    private Integer sort;

    /**
     * 是否在回收站 1是0否
     */
    private Integer isRecycle;

    /**
     * 删除时间戳，0表示未删除
     */
    private Long deletedAt;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
