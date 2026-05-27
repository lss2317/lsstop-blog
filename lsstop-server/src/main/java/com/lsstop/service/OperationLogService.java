package com.lsstop.service;

import com.lsstop.domain.entity.OperationLogEntity;
import com.lsstop.domain.vo.OperationLogVO;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * 操作日志服务
 *
 * @author lishusheng
 * @date 2026/03/29
 */
public interface OperationLogService {

    /**
     * 插入操作日志
     *
     * @param operationLog 操作日志实体
     */
    void insert(OperationLogEntity operationLog);

    /**
     * 分页查询操作日志列表
     *
     * @param current       当前页码
     * @param pageSize      每页数量
     * @param module        操作模块（模糊搜索）
     * @param operationType 操作类型
     * @param userId        用户ID
     * @return 操作日志列表
     */
    List<OperationLogVO> listOperationLogs(Integer current, Integer pageSize,
                                           String module, String operationType, String userId);

    /**
     * 统计操作日志总数
     *
     * @param module        操作模块（模糊搜索）
     * @param operationType 操作类型
     * @param userId        用户ID
     * @return 操作日志总数
     */
    Integer countTotal(String module, String operationType, String userId);

    /**
     * 删除操作日志（支持单个和批量删除）
     *
     * @param logNumbers 日志编号列表
     */
    void deleteByLogNumbers(List<String> logNumbers);

    /**
     * 导出操作日志为 Excel
     *
     * @param module        操作模块（模糊搜索）
     * @param operationType 操作类型
     * @param userId        用户ID
     * @param response      HTTP响应，用于写出Excel文件
     */
    void exportOperationLogs(String module, String operationType, String userId,
                             HttpServletResponse response) throws IOException;

}
