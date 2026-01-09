package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.common.Result;
import com.lsstop.domain.dataObject.PhotoAlbumDO;
import com.lsstop.domain.vo.PhotoAlbumVO;
import com.lsstop.domain.vo.PhotoAlbumInfoVO;
import com.lsstop.service.PhotoAlbumService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 相册控制层
 *
 * @author lishusheng
 * @date 2025/12/27
 */
@RestController
public class PhotoAlbumController {

    @Resource
    private PhotoAlbumService photoAlbumService;

    /**
     * 获取所有相册
     *
     * @return 相册列表
     */
    @GetMapping("/front/photoAlbum/listPhotoAlbum")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<List<PhotoAlbumVO>> getPhotoAlbumList() {
        List<PhotoAlbumDO> photoAlbumList = photoAlbumService.getPhotoAlbumList();
        List<PhotoAlbumVO> list = photoAlbumList.stream().map(photoAlbum -> photoAlbum.asViewObject(PhotoAlbumVO.class)).toList();
        return Result.success(list);
    }

    /**
     * 根据ID获取相册信息
     *
     * @param id 相册ID
     * @return 相册名称和封面
     */
    @GetMapping("/front/photoAlbum/{id}")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<PhotoAlbumInfoVO> getPhotoAlbumById(@PathVariable Integer id) {
        PhotoAlbumDO photoAlbum = photoAlbumService.getPhotoAlbumById(id);
        if (photoAlbum == null) {
            return Result.success(null);
        }
        return Result.success(photoAlbum.asViewObject(PhotoAlbumInfoVO.class));
    }

}
