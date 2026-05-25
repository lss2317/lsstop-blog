package com.lsstop.mapper;

import com.lsstop.domain.entity.OperationLogEntity;
import com.lsstop.domain.vo.OperationLogVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 操作日志数据访问层
 *
 * @author lishusheng
 * @date 2026/03/29
 */
public interface OperationLogMapper {

    /**
     * 插入操作日志
     *
     * @param operationLog 操作日志实体
     */
    void insert(OperationLogEntity operationLog);

    /**
     * 分页查询操作日志列表
     *
     * @param offset        偏移量
     * @param size          每页数量
     * @param module        操作模块（模糊搜索）
     * @param operationType 操作类型
     * @param userId        用户ID
     * @return 操作日志列表
     */
    List<OperationLogVO> selectList(@Param("offset") Integer offset,
                                    @Param("size") Integer size,
                                    @Param("module") String module,
                                    @Param("operationType") String operationType,
                                    @Param("userId") String userId);

    /**
     * 统计操作日志总数
     *
     * @param module        操作模块（模糊搜索）
     * @param operationType 操作类型
     * @param userId        用户ID
     * @return 操作日志总数
     */
    Integer countTotal(@Param("module") String module,
                       @Param("operationType") String operationType,
                       @Param("userId") String userId);

    /**
     * 软删除操作日志（支持单个和批量删除）
     *
     * @param logNumbers 日志编号列表
     * @param deletedAt  删除时间戳
     */
    void deleteByLogNumbers(@Param("logNumbers") List<String> logNumbers, @Param("deletedAt") Long deletedAt);

}
