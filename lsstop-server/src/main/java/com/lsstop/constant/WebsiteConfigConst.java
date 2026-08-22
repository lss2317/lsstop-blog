package com.lsstop.constant;

/**
 * 网站配置模块常量
 *
 * @author lishusheng
 * @date 2026/08/22
 */
public class WebsiteConfigConst {

    private WebsiteConfigConst() {
    }

    /**
     * 网站配置不存在
     */
    public static final String WEBSITE_CONFIG_NOT_FOUND = "网站配置不存在";

    /**
     * QQ链接格式不正确
     */
    public static final String QQ_URL_INVALID = "QQ链接必须是有效的HTTP或HTTPS地址";

    /**
     * GitHub链接格式不正确
     */
    public static final String GITHUB_URL_INVALID = "GitHub链接必须是有效的HTTP或HTTPS地址";

    /**
     * Gitee链接格式不正确
     */
    public static final String GITEE_URL_INVALID = "Gitee链接必须是有效的HTTP或HTTPS地址";

    /**
     * WebSocket地址格式不正确
     */
    public static final String WEBSOCKET_URL_INVALID = "WebSocket地址格式不正确，请输入localhost:8080或wss://example.com";
}
