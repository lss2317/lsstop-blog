package com.lsstop.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 第三方OAuth登录配置
 *
 * @author lishusheng
 * @date 2026/03/22
 */
@Data
@Component
@ConfigurationProperties(prefix = "oauth")
public class OAuthConfig {

    /**
     * QQ OAuth 配置
     */
    private QQ qq;

    /**
     * 微博 OAuth 配置
     */
    private Weibo weibo;

    @Data
    public static class QQ {
        /** App ID */
        private String appId;
        /** App Key */
        private String appKey;
        /** 回调地址 */
        private String redirectUri;
    }

    @Data
    public static class Weibo {
        /** App Key */
        private String appKey;
        /** App Secret */
        private String appSecret;
        /** 回调地址 */
        private String redirectUri;
    }
}
