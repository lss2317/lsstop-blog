package com.lsstop.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT配置类
 *
 * @author lishusheng
 * @date 2026/01/03
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    /**
     * 密钥
     */
    private String secret;

    /**
     * 前台配置
     */
    private TokenConfig front;

    /**
     * 后台配置
     */
    private TokenConfig admin;

    /**
     * Token配置
     */
    @Data
    public static class TokenConfig {
        /**
         * Access Token 过期时间（毫秒）
         */
        private Long accessTokenExpiration;

        /**
         * Refresh Token 过期时间（毫秒）
         */
        private Long refreshTokenExpiration;
    }
}
