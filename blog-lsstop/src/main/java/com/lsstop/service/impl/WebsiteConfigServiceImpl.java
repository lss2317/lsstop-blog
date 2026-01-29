package com.lsstop.service.impl;

import com.lsstop.constant.RedisConst;
import com.lsstop.domain.entity.WebsiteConfigEntity;
import com.lsstop.mapper.WebsiteConfigMapper;
import com.lsstop.service.WebsiteConfigService;
import com.lsstop.utils.RedisUtils;
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

    @Resource
    private RedisUtils redisUtils;

    /**
     * 获取网站配置信息，优先从Redis获取，没有则查DB并缓存
     *
     * @return 网站配置实体对象
     */
    @Override
    public WebsiteConfigEntity getWebsiteConfig() {
        // 优先从Redis获取
        WebsiteConfigEntity config = redisUtils.get(RedisConst.WEBSITE_CONFIG, WebsiteConfigEntity.class);
        if (config != null) {
            return config;
        }
        // Redis中没有，查询DB
        config = websiteConfigMapper.getWebsiteConfig();
        if (config != null) {
            // 缓存到1天
            redisUtils.set(RedisConst.WEBSITE_CONFIG, config, RedisConst.EXPIRE_ONE_DAY);
        }
        return config;
    }

}
