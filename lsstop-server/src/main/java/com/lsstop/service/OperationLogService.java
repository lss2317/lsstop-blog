package com.lsstop.service;

import com.lsstop.domain.entity.OperationLogEntity;
import com.lsstop.domain.vo.OperationLogVO;

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

}
