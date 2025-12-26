package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.common.Result;
import com.lsstop.domain.entity.WebsiteConfig;
import com.lsstop.domain.vo.WebsiteConfigVo;
import com.lsstop.service.WebsiteConfigService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 网站配置信息控制层
 *
 * @author lishusheng
 * @date 2025/12/25
 */
@RestController
@RequestMapping("/websiteConfig")
public class WebsiteConfigController {

    @Resource
    private WebsiteConfigService websiteConfigService;

    /**
     * 获取网站配置信息
     *
     * @return 网站配置信息
     */
    @GetMapping("/getWebsiteConfig")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<WebsiteConfigVo> getWebsiteConfig() {
        WebsiteConfig websiteConfig = websiteConfigService.getWebsiteConfig();
        return Result.success(websiteConfig.asViewObject(WebsiteConfigVo.class));
    }
}
