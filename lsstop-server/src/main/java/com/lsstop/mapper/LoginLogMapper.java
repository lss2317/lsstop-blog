package com.lsstop.mapper;

import com.lsstop.domain.entity.LoginLogEntity;
import org.apache.ibatis.annotations.Param;

/**
 * 登录日志数据访问层
 *
 * @author lishusheng
 * @date 2026/01/17
 */
public interface LoginLogMapper {

    /**
     * 插入登录日志
     *
     * @param loginLog 登录日志实体
     */
    void insert(LoginLogEntity loginLog);

    /**
     * 查询用户最近一次成功登录的IP归属地
     *
     * @param userId 用户ID
     * @return IP归属地
     */
    String selectLatestIpRegionByUserId(@Param("userId") String userId);

}
