package com.lsstop.service.impl;

import com.lsstop.domain.entity.WebsiteConfig;
import com.lsstop.mapper.WebsiteConfigMapper;
import com.lsstop.service.WebsiteConfigService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 网站配置服务实现类
 *
 * @author lishusheng
 * @date 2025/12/25
 */
@Service
public class WebsiteConfigServiceImpl implements WebsiteConfigService {

    @Resource
    private WebsiteConfigMapper websiteConfigMapper;

    /**
     * 获取网站配置信息
     *
     * @return 网站配置实体对象
     */
    @Override
    public WebsiteConfig getWebsiteConfig() {
        return websiteConfigMapper.getWebsiteConfig();
    }

}
