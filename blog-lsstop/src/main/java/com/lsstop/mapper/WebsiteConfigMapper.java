package com.lsstop.mapper;

import com.lsstop.domain.entity.WebsiteConfig;

/**
 * 网站配置数据访问层
 *
 * @author lishusheng
 * @date 2025/12/25
 */
public interface WebsiteConfigMapper {

    /**
     * 查询网站配置信息
     *
     * @return 网站配置实体对象
     */
    WebsiteConfig getWebsiteConfig();
}
