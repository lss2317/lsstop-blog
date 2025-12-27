package com.lsstop.domain.vo;

import lombok.Data;

/**
 * @author lishusheng
 * @date 2025/12/27
 */
@Data
public class PhotoAlbumVo {

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

}
