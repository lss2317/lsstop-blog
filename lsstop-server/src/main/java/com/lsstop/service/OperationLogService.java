package com.lsstop.service;

import com.lsstop.domain.entity.OperationLogEntity;

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

}
