package com.lsstop.domain.vo;

import lombok.Data;

/**
 * 照片详情VO
 *
 * @author lishusheng
 * @date 2026/01/08
 */
@Data
public class PhotoAlbumImageVO {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 照片地址
     */
    private String photoSrc;


}
