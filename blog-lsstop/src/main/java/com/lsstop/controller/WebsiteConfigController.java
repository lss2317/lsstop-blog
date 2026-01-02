package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.common.Result;
import com.lsstop.domain.dataObject.WebsiteConfigDO;
import com.lsstop.domain.vo.WebsiteConfigVO;
import com.lsstop.service.WebsiteConfigService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 网站配置信息控制层
 *
 * @author lishusheng
 * @date 2025/12/25
 */
@RestController
public class WebsiteConfigController {

    @Resource
    private WebsiteConfigService websiteConfigService;

    /**
     * 获取网站配置信息
     *
     * @return 网站配置信息
     */
    @GetMapping("/front/websiteConfig/getWebsiteConfig")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<WebsiteConfigVO> getWebsiteConfig() {
        WebsiteConfigDO websiteConfig = websiteConfigService.getWebsiteConfig();
        return Result.success(websiteConfig.asViewObject(WebsiteConfigVO.class));
    }
}
