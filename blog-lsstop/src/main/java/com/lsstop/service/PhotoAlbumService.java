package com.lsstop.service;

import com.lsstop.domain.entity.PhotoAlbum;

import java.util.List;

/**
 * 相册服务
 *
 * @author lishusheng
 * @date 2025/12/27
 */
public interface PhotoAlbumService {


    /**
     * 获取所有相册
     *
     * @return 相册列表
     */
    List<PhotoAlbum> getPhotoAlbumList();

}
