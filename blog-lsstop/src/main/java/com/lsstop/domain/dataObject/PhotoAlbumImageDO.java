package com.lsstop.domain.dataObject;

import com.lsstop.domain.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 照片详情DO
 *
 * @author lishusheng
 * @date 2026/01/08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotoAlbumImageDO implements BaseData {

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
     * 是否删除 1是0否
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
