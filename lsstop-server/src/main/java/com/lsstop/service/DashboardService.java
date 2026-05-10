package com.lsstop.service;

import com.lsstop.domain.vo.AnalysisDataVO;
import com.lsstop.domain.vo.ConsoleDataVO;

/**
 * 仪表盘服务接口
 *
 * @author lishusheng
 * @date 2026/05/08
 */
public interface DashboardService {

    /**
     * 获取主页（控制台）聚合数据
     *
     * @return 控制台聚合数据
     */
    ConsoleDataVO getConsoleData();

    /**
     * 获取分析页聚合数据
     *
     * @return 分析页聚合数据
     */
    AnalysisDataVO getAnalysisData();
}
