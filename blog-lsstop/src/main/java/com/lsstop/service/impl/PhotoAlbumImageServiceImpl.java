package com.lsstop.service.impl;

import com.lsstop.domain.dataObject.PhotoAlbumImageDO;
import com.lsstop.mapper.PhotoAlbumImageMapper;
import com.lsstop.service.PhotoAlbumImageService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 照片详情服务实现类
 *
 * @author lishusheng
 * @date 2026/01/08
 */
@Service
public class PhotoAlbumImageServiceImpl implements PhotoAlbumImageService {

    @Resource
    private PhotoAlbumImageMapper photoAlbumImageMapper;

    /**
     * 根据相册id获取照片列表
     *
     * @param albumId 相册id
     * @return 照片列表
     */
    @Override
    public List<PhotoAlbumImageDO> getPhotoListByAlbumId(Integer albumId) {
        return photoAlbumImageMapper.getPhotoListByAlbumId(albumId);
    }

}
