package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.annotation.OperationLog;
import com.lsstop.common.Result;
import com.lsstop.enums.FileFolderEnum;
import com.lsstop.enums.OperationModuleEnum;
import com.lsstop.enums.OperationTypeEnum;
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
     * 上传文章图片
     *
     * @param file 图片文件
     * @return 图片访问URL
     */
    @PostMapping("/admin/file/article")
    @AccessLimit(seconds = 60, maxCount = 30)
    @OperationLog(module = OperationModuleEnum.FILE, type = OperationTypeEnum.UPLOAD, description = "上传文章图片")
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
    @AccessLimit(seconds = 60, maxCount = 30)
    @OperationLog(module = OperationModuleEnum.FILE, type = OperationTypeEnum.UPLOAD, description = "上传相册图片")
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
    @AccessLimit(seconds = 60, maxCount = 30)
    @OperationLog(module = OperationModuleEnum.FILE, type = OperationTypeEnum.UPLOAD, description = "上传说说图片")
    public Result<String> uploadTalkImage(@RequestParam("file") MultipartFile file) {
        String url = cosService.uploadImage(file, FileFolderEnum.TALK.getFolder());
        return Result.success(url);
    }

    /**
     * 上传聊天室图片
     *
     * @param file 图片文件
     * @return 图片访问URL
     */
    @PostMapping("/front/chat/image")
    @AccessLimit(seconds = 60, maxCount = 10)
    public Result<String> uploadChatImage(@RequestParam("file") MultipartFile file) {
        String url = cosService.uploadImage(file, FileFolderEnum.CHAT.getFolder());
        return Result.success(url);
    }
}
