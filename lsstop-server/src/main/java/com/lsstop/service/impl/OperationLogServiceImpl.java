package com.lsstop.service.impl;

import com.lsstop.domain.entity.OperationLogEntity;
import com.lsstop.domain.vo.OperationLogVO;
import com.lsstop.mapper.OperationLogMapper;
import com.lsstop.service.OperationLogService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

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
    @Override
    public List<OperationLogVO> listOperationLogs(Integer current, Integer pageSize,
                                                  String module, String operationType, String userId) {
        int offset = (current - 1) * pageSize;
        return operationLogMapper.selectList(offset, pageSize, module, operationType, userId);
    }

    /**
     * 统计操作日志总数
     *
     * @param module        操作模块（模糊搜索）
     * @param operationType 操作类型
     * @param userId        用户ID
     * @return 操作日志总数
     */
    @Override
    public Integer countTotal(String module, String operationType, String userId) {
        return operationLogMapper.countTotal(module, operationType, userId);
    }

    /**
     * 删除操作日志（支持单个和批量删除）
     *
     * @param logNumbers 日志编号列表
     */
    @Override
    public void deleteByLogNumbers(List<String> logNumbers) {
        operationLogMapper.deleteByLogNumbers(logNumbers, System.currentTimeMillis());
    }

}
