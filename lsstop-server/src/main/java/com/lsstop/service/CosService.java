package com.lsstop.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * COS 文件服务接口
 *
 * @author lss
 * @date 2026/3/16
 */
public interface CosService {

    /**
     * 上传图片
     *
     * @param file   图片文件
     * @param folder 目标文件夹（如：avatar、article）
     * @return 图片访问URL
     */
    String uploadImage(MultipartFile file, String folder);

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     */
    void deleteFile(String fileUrl);
}
