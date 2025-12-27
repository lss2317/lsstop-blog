package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.common.Result;
import com.lsstop.domain.entity.PhotoAlbum;
import com.lsstop.domain.vo.PhotoAlbumVo;
import com.lsstop.service.PhotoAlbumService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 相册控制层
 *
 * @author lishusheng
 * @date 2025/12/27
 */
@RestController
@RequestMapping("photoAlbum")
public class PhotoAlbumController {

    @Resource
    private PhotoAlbumService photoAlbumService;

    /**
     * 获取所有相册
     *
     * @return 相册列表
     */
    @GetMapping("/listPhotoAlbum")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<List<PhotoAlbumVo>> getPhotoAlbumList() {
        List<PhotoAlbum> photoAlbumList = photoAlbumService.getPhotoAlbumList();
        List<PhotoAlbumVo> list = photoAlbumList.stream().map(photoAlbum -> photoAlbum.asViewObject(PhotoAlbumVo.class)).toList();
        return Result.success(list);
    }

}
