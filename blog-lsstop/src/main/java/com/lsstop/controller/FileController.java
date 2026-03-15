package com.lsstop.controller;

import com.lsstop.common.Result;
import com.lsstop.enums.FileFolderEnum;
import com.lsstop.service.CosService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器
 *
 * @author lss
 * @date 2026/3/16
 */
@RestController
public class FileController {

    @Resource
    private CosService cosService;

    /**
     * 上传头像
     *
     * @param file 头像文件
     * @return 头像访问URL
     */
    @PostMapping("/front/file/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        String url = cosService.uploadImage(file, FileFolderEnum.AVATAR.getFolder());
        return Result.success(url);
    }

    /**
     * 上传文章图片
     *
     * @param file 图片文件
     * @return 图片访问URL
     */
    @PostMapping("/admin/file/article")
    public Result<String> uploadArticleImage(@RequestParam("file") MultipartFile file) {
        String url = cosService.uploadImage(file, FileFolderEnum.ARTICLE.getFolder());
        return Result.success(url);
    }

    /**
     * 上传相册图片
     *
     * @param file 图片文件
     * @return 图片访问URL
     */
    @PostMapping("/admin/file/album")
    public Result<String> uploadAlbumImage(@RequestParam("file") MultipartFile file) {
        String url = cosService.uploadImage(file, FileFolderEnum.ALBUM.getFolder());
        return Result.success(url);
    }

    /**
     * 上传说说图片
     *
     * @param file 图片文件
     * @return 图片访问URL
     */
    @PostMapping("/admin/file/talk")
    public Result<String> uploadTalkImage(@RequestParam("file") MultipartFile file) {
        String url = cosService.uploadImage(file, FileFolderEnum.TALK.getFolder());
        return Result.success(url);
    }
}
