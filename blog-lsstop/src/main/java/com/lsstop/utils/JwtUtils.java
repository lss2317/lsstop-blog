package com.lsstop.utils;

import com.lsstop.config.JwtConfig;
import com.lsstop.constant.AuthConst;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT工具类（双Token机制）
 *
 * @author lishusheng
 * @date 2026/01/03
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final JwtConfig jwtConfig;

    /**
     * 签发者
     */
    private static final String ISSUER = "lsstop-blog";

    /**
     * Token类型标识Key
     */
    private static final String TOKEN_TYPE_KEY = "token_type";

    /**
     * Token来源标识Key（front-前台，admin-后台）
     */
    private static final String SOURCE_KEY = "source";

    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";

    /**
     * Token对（包含AccessToken和RefreshToken）
     */
    @Data
    @AllArgsConstructor
    public static class TokenPair {
        private String accessToken;
        private String refreshToken;
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成前台Token对
     *
     * @param subject 主题（用户ID）
     * @return TokenPair
     */
    public TokenPair generateFrontTokenPair(String subject) {
        JwtConfig.TokenConfig config = jwtConfig.getFront();
        String accessToken = buildToken(subject, ACCESS_TOKEN_TYPE, AuthConst.SOURCE_FRONT, config.getAccessTokenExpiration());
        String refreshToken = buildToken(subject, REFRESH_TOKEN_TYPE, AuthConst.SOURCE_FRONT, config.getRefreshTokenExpiration());
        return new TokenPair(accessToken, refreshToken);
    }

    /**
     * 生成后台Token对
     *
     * @param subject 主题（用户ID）
     * @return TokenPair
     */
    public TokenPair generateAdminTokenPair(String subject) {
        JwtConfig.TokenConfig config = jwtConfig.getAdmin();
        String accessToken = buildToken(subject, ACCESS_TOKEN_TYPE, AuthConst.SOURCE_ADMIN, config.getAccessTokenExpiration());
        String refreshToken = buildToken(subject, REFRESH_TOKEN_TYPE, AuthConst.SOURCE_ADMIN, config.getRefreshTokenExpiration());
        return new TokenPair(accessToken, refreshToken);
    }

    private String buildToken(String subject, String tokenType, String source, long expiration) {
        Date now = new Date();
        return Jwts.builder()
                .subject(subject)
                .issuer(ISSUER)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration))
                .claim(TOKEN_TYPE_KEY, tokenType)
                .claim(SOURCE_KEY, source)
                .signWith(getSigningKey())
                .compact();
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 获取Token中的主题信息（通常为用户ID）
     *
     * @param token Token字符串
     * @return 主题信息
     */
    public String getSubject(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * 获取Token中的主题信息（忽略过期校验）
     * <p>用于退出登录等场景，即使token过期也需要解析出userId</p>
     *
     * @param token Token字符串
     * @return 主题信息，解析失败返回null
     */
    public String getSubjectIgnoreExpiration(String token) {
        try {
            return parseToken(token).getSubject();
        } catch (ExpiredJwtException e) {
            // token过期时，仍可从异常中获取claims
            return e.getClaims().getSubject();
        } catch (Exception e) {
            log.warn("Token解析失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取Token来源（front-前台，admin-后台）
     *
     * @param token Token字符串
     * @return 来源标识
     */
    public String getSource(String token) {
        return parseToken(token).get(SOURCE_KEY, String.class);
    }

    /**
     * 验证AccessToken是否有效（签名、过期时间、Token类型）
     *
     * @param token Token字符串
     * @return true-有效，false-无效或已过期
     */
    public boolean validateAccessToken(String token) {
        return validateToken(token, ACCESS_TOKEN_TYPE);
    }

    /**
     * 验证RefreshToken是否有效（签名、过期时间、Token类型）
     *
     * @param token Token字符串
     * @return true-有效，false-无效或已过期
     */
    public boolean validateRefreshToken(String token) {
        return validateToken(token, REFRESH_TOKEN_TYPE);
    }

    private boolean validateToken(String token, String expectedType) {
        try {
            Claims claims = parseToken(token);
            return expectedType.equals(claims.get(TOKEN_TYPE_KEY));
        } catch (ExpiredJwtException e) {
            log.warn("Token已过期: {}", e.getMessage());
        } catch (JwtException e) {
            log.warn("Token无效: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Token验证异常: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 使用RefreshToken刷新前台AccessToken
     *
     * @param refreshToken RefreshToken
     * @return 新的AccessToken，无效则返回null
     */
    public String refreshFrontAccessToken(String refreshToken) {
        if (validateRefreshToken(refreshToken)) {
            String subject = getSubject(refreshToken);
            return buildToken(subject, ACCESS_TOKEN_TYPE, AuthConst.SOURCE_FRONT, jwtConfig.getFront().getAccessTokenExpiration());
        }
        return null;
    }

    /**
     * 使用RefreshToken刷新后台AccessToken
     *
     * @param refreshToken RefreshToken
     * @return 新的AccessToken，无效则返回null
     */
    public String refreshAdminAccessToken(String refreshToken) {
        if (validateRefreshToken(refreshToken)) {
            String subject = getSubject(refreshToken);
            return buildToken(subject, ACCESS_TOKEN_TYPE, AuthConst.SOURCE_ADMIN, jwtConfig.getAdmin().getAccessTokenExpiration());
        }
        return null;
    }
}
