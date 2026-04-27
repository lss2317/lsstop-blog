package com.lsstop.service;

import com.lsstop.domain.entity.PhotoAlbumImageEntity;

import java.util.List;

/**
 * 照片详情服务
 *
 * @author lishusheng
 * @date 2026/01/08
 */
public interface PhotoAlbumImageService {

    /**
     * 根据相册id获取照片列表
     *
     * @param albumId 相册id
     * @return 照片列表
     */
    List<PhotoAlbumImageEntity> getPhotoListByAlbumId(Integer albumId);

}
