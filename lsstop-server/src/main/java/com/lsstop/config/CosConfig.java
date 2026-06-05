package com.lsstop.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.region.Region;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 腾讯云 COS 配置
 *
 * @author lss
 * @date 2026/3/16
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "cos")
public class CosConfig {

    /**
     * 腾讯云 SecretId
     */
    private String secretId;

    /**
     * 腾讯云 SecretKey
     */
    private String secretKey;

    /**
     * 存储桶所在地域
     */
    private String region;

    /**
     * 存储桶名称
     */
    private String bucketName;

    /**
     * 访问域名（用于拼接文件URL）
     */
    private String domain;

    /**
     * 上传目录前缀
     */
    private String pathPrefix;

    /**
     * 创建 COS 客户端
     */
    @Bean(destroyMethod = "shutdown")
    public COSClient cosClient() {
        // 初始化身份信息
        COSCredentials credentials = new BasicCOSCredentials(secretId, secretKey);
        // 设置地域
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        // 使用 HTTPS 协议
        clientConfig.setHttpProtocol(HttpProtocol.https);
        return new COSClient(credentials, clientConfig);
    }
}
