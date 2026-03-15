package com.lsstop.service.impl;

import com.lsstop.config.CosConfig;
import com.lsstop.constant.FileConst;
import com.lsstop.exception.BusinessException;
import com.lsstop.service.CosService;
import com.lsstop.utils.FileUtils;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * COS 文件服务实现类
 *
 * @author lss
 * @date 2026/3/16
 */
@Slf4j
@Service
public class CosServiceImpl implements CosService {

    @Resource
    private COSClient cosClient;

    @Resource
    private CosConfig cosConfig;

    /**
     * 上传图片
     *
     * @param file   图片文件
     * @param folder 目标文件夹（如：avatar、article）
     * @return 图片访问URL
     */
    @Override
    public String uploadImage(MultipartFile file, String folder) {
        // 校验图片
        validateImageFile(file);
        // 生成文件名
        String fileName = FileUtils.generateUniqueFileName(file.getOriginalFilename());
        // 拼接完整路径
        String key = cosConfig.getPathPrefix() + folder + "/" + fileName;

        try (InputStream inputStream = file.getInputStream()) {
            // 设置文件元数据
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            // 上传文件
            PutObjectRequest putRequest = new PutObjectRequest(
                    cosConfig.getBucketName(), key, inputStream, metadata
            );
            cosClient.putObject(putRequest);
            // 返回访问URL
            return cosConfig.getDomain() + "/" + key;
        } catch (Exception e) {
            log.error("图片上传失败: {}", e.getMessage(), e);
            throw new BusinessException(FileConst.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     */
    @Override
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }
        try {
            // 从 URL 中提取 key
            String key = fileUrl.replace(cosConfig.getDomain() + "/", "");
            cosClient.deleteObject(cosConfig.getBucketName(), key);
        } catch (Exception e) {
            log.error("文件删除失败: {}", e.getMessage(), e);
            throw new BusinessException(FileConst.FILE_DELETE_FAILED);
        }
    }

    /**
     * 校验文件（双重校验：扩展名 + MIME类型）
     */
    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(FileConst.FILE_EMPTY);
        }
        if (file.getSize() > FileConst.MAX_FILE_SIZE) {
            throw new BusinessException(FileConst.FILE_TOO_LARGE);
        }
        // 校验文件扩展名
        String extension = FileUtils.getExtension(file.getOriginalFilename());
        if (!FileConst.ALLOWED_IMAGE_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BusinessException(FileConst.FILE_TYPE_NOT_SUPPORTED);
        }
        // 校验 MIME 类型
        String contentType = file.getContentType();
        if (contentType == null || !FileConst.ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new BusinessException(FileConst.FILE_TYPE_NOT_SUPPORTED);
        }
    }
}
