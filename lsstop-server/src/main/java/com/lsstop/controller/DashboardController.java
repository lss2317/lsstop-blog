package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.common.Result;
import com.lsstop.domain.vo.AnalysisDataVO;
import com.lsstop.domain.vo.ConsoleDataVO;
import com.lsstop.service.DashboardService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 仪表盘控制层
 *
 * @author lishusheng
 * @date 2026/05/08
 */
@RestController
public class DashboardController {

    @Resource
    private DashboardService dashboardService;

    /**
     * 获取主页（控制台）聚合数据
     *
     * @return 控制台聚合数据
     */
    @AccessLimit(seconds = 60, maxCount = 60)
    @GetMapping("/admin/dashboard/console")
    public Result<ConsoleDataVO> getConsoleData() {
        return Result.success(dashboardService.getConsoleData());
    }

    /**
     * 获取分析页聚合数据
     *
     * @return 分析页聚合数据
     */
    @AccessLimit(seconds = 60, maxCount = 60)
    @GetMapping("/admin/dashboard/analysis")
    public Result<AnalysisDataVO> getAnalysisData() {
        return Result.success(dashboardService.getAnalysisData());
    }
}
