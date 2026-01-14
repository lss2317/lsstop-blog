package com.lsstop.mapper;

import com.lsstop.domain.entity.PhotoAlbumImageEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 照片详情数据访问层
 *
 * @author lishusheng
 * @date 2026/01/08
 */
public interface PhotoAlbumImageMapper {

    /**
     * 根据相册id获取照片列表
     *
     * @param albumId 相册id
     * @return 照片列表
     */
    List<PhotoAlbumImageEntity> getPhotoListByAlbumId(@Param("albumId") Integer albumId);

}
