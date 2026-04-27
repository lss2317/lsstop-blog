package com.lsstop.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 博客配置
 *
 * @author lss
 * @date 2026/2/14
 */
@Data
@Component
@ConfigurationProperties(prefix = "blog")
public class BlogConfig {

    /**
     * 博客名称
     */
    private String name;

    /**
     * 博主邮箱
     */
    private String ownerEmail;

    /**
     * 博客前台地址
     */
    private String url;

    /**
     * 默认用户昵称前缀
     */
    private String defaultNicknamePrefix;
}
