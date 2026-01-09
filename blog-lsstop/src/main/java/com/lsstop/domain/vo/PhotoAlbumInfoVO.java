package com.lsstop.domain.vo;

import lombok.Data;

/**
 * 相册基本信息VO
 *
 * @author lishusheng
 * @date 2026/01/09
 */
@Data
public class PhotoAlbumInfoVO {

    /**
     * 相册名
     */
    private String photoAlbumName;

    /**
     * 相册封面
     */
    private String photoAlbumCover;

}
