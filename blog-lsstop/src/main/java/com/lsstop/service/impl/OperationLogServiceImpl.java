package com.lsstop.service.impl;

import com.lsstop.domain.entity.OperationLogEntity;
import com.lsstop.mapper.OperationLogMapper;
import com.lsstop.service.OperationLogService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 操作日志服务实现类
 *
 * @author lishusheng
 * @date 2026/03/29
 */
@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Resource
    private OperationLogMapper operationLogMapper;

    /**
     * 插入操作日志
     *
     * @param operationLog 操作日志实体
     */
    @Override
    public void insert(OperationLogEntity operationLog) {
        operationLogMapper.insert(operationLog);
    }

}
