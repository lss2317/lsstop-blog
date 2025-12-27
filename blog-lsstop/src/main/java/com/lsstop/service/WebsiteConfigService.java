package com.lsstop.service;

import com.lsstop.domain.entity.WebsiteConfig;

/**
 * 网站配置服务
 *
 * @author lishusheng
 * @date 2025/12/25
 */
public interface WebsiteConfigService {

    /**
     * 获取网站配置信息
     *
     * @return 网站配置实体对象
     */
    WebsiteConfig getWebsiteConfig();
}
