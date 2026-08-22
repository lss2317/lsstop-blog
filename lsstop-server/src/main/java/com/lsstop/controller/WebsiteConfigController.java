package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.annotation.OperationLog;
import com.lsstop.common.Result;
import com.lsstop.domain.dto.UpdateWebsiteConfigDTO;
import com.lsstop.domain.entity.WebsiteConfigEntity;
import com.lsstop.domain.vo.VisitStatsVO;
import com.lsstop.domain.vo.WebsiteConfigAdminVO;
import com.lsstop.domain.vo.WebsiteConfigVO;
import com.lsstop.enums.OperationModuleEnum;
import com.lsstop.enums.OperationTypeEnum;
import com.lsstop.service.WebsiteConfigService;
import com.lsstop.utils.IpUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
        WebsiteConfigEntity websiteConfig = websiteConfigService.getWebsiteConfig();
        return Result.success(websiteConfig.asViewObject(WebsiteConfigVO.class));
    }

    /**
     * 获取网站配置后台管理信息
     *
     * @return 网站配置后台管理信息
     */
    @GetMapping("/admin/setting/info")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<WebsiteConfigAdminVO> getAdminWebsiteConfig() {
        WebsiteConfigEntity websiteConfig = websiteConfigService.getWebsiteConfig();
        return Result.success(websiteConfig.asViewObject(WebsiteConfigAdminVO.class));
    }

    /**
     * 更新网站配置
     *
     * @param dto 网站配置更新参数
     * @return 操作结果
     */
    @PutMapping("/admin/setting/update")
    @AccessLimit(seconds = 60, maxCount = 20)
    @OperationLog(module = OperationModuleEnum.WEBSITE_CONFIG, type = OperationTypeEnum.UPDATE, description = "更新网站配置")
    public Result<Void> updateAdminWebsiteConfig(@RequestBody @Validated UpdateWebsiteConfigDTO dto) {
        websiteConfigService.updateWebsiteConfig(dto);
        return Result.success();
    }

    /**
     * 上报访问并获取总访问量
     *
     * @param request 请求对象
     * @return 访问统计信息
     */
    @GetMapping("/front/websiteConfig/visit/report")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<VisitStatsVO> reportVisit(HttpServletRequest request) {
        String ipAddress = IpUtils.getIpAddress(request);
        VisitStatsVO visitStats = websiteConfigService.reportVisit(ipAddress);
        return Result.success(visitStats);
    }
}
