package com.lsstop.mapper;

import com.lsstop.domain.entity.PhotoAlbum;

import java.util.List;

/**
 * 相册数据访问层
 *
 * @author lishusheng
 * @date 2025/12/27
 */
public interface PhotoAlbumMapper {

    /**
     * 获取所有相册
     *
     * @return 相册列表
     */
    List<PhotoAlbum> getPhotoAlbumList();

}
