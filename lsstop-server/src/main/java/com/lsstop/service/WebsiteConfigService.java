package com.lsstop.service;

import com.lsstop.domain.entity.WebsiteConfigEntity;
import com.lsstop.domain.vo.VisitStatsVO;

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
    WebsiteConfigEntity getWebsiteConfig();

    /**
     * 上报访问并获取访问统计
     *
     * @param ipAddress 访客IP地址
     * @return 访问统计信息
     */
    VisitStatsVO reportVisit(String ipAddress);
}
