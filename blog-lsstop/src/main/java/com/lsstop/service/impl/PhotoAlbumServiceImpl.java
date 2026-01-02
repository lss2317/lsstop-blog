package com.lsstop.service.impl;

import com.lsstop.domain.dataObject.PhotoAlbumDO;
import com.lsstop.mapper.PhotoAlbumMapper;
import com.lsstop.service.PhotoAlbumService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 相册服务实现类
 *
 * @author lishusheng
 * @date 2025/12/27
 */
@Service
public class PhotoAlbumServiceImpl implements PhotoAlbumService {

    @Resource
    private PhotoAlbumMapper photoAlbumMapper;


    /**
     * 获取所有相册
     *
     * @return 相册列表
     */
    @Override
    public List<PhotoAlbumDO> getPhotoAlbumList() {
        return photoAlbumMapper.getPhotoAlbumList();
    }
}
