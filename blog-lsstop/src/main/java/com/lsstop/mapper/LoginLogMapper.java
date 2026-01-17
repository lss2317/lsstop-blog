package com.lsstop.mapper;

import com.lsstop.domain.entity.LoginLogEntity;

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

}
