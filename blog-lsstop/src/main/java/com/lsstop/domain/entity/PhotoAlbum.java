package com.lsstop.domain.entity;

import com.lsstop.domain.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 相册实体类
 *
 * @author lishusheng
 * @date 2025/12/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotoAlbum implements BaseData {

    /**
     * id
     */
    private Integer id;

    /**
     * 相册名
     */
    private String photoAlbumName;

    /**
     * 相册描述
     */
    private String photoAlbumDesc;

    /**
     * 相册封面
     */
    private String photoAlbumCover;

    /**
     * 状态值 1公开 2私密
     */
    private Integer status;

    /**
     * 是否删除
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
