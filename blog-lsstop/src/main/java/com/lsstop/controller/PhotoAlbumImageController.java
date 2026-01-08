package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.common.Result;
import com.lsstop.domain.dataObject.PhotoAlbumImageDO;
import com.lsstop.domain.vo.PhotoAlbumImageVO;
import com.lsstop.service.PhotoAlbumImageService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 照片详情控制层
 *
 * @author lishusheng
 * @date 2026/01/08
 */
@RestController
public class PhotoAlbumImageController {

    @Resource
    private PhotoAlbumImageService photoAlbumImageService;

    /**
     * 根据相册id获取照片列表
     *
     * @param albumId 相册id
     * @return 照片列表
     */
    @GetMapping("/front/photoAlbum/listPhoto")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<List<PhotoAlbumImageVO>> getPhotoListByAlbumId(@RequestParam Integer albumId) {
        List<PhotoAlbumImageDO> photoList = photoAlbumImageService.getPhotoListByAlbumId(albumId);
        List<PhotoAlbumImageVO> list = photoList.stream().map(photo -> photo.asViewObject(PhotoAlbumImageVO.class)).toList();
        return Result.success(list);
    }

}
