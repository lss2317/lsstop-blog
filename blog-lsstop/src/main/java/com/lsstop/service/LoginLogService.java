package com.lsstop.service;

import com.lsstop.domain.entity.LoginLogEntity;

/**
 * 登录日志服务
 *
 * @author lishusheng
 * @date 2026/01/17
 */
public interface LoginLogService {

    /**
     * 插入登录日志
     *
     * @param loginLog 登录日志实体
     */
    void insert(LoginLogEntity loginLog);

    /**
     * 发送登录日志到MQ
     *
     * @param userId    用户ID
     * @param loginType 登录方式
     * @param source    登录来源
     * @param state     登录结果
     * @param message   登录信息
     */
    void sendLoginLog(String userId, Integer loginType, Integer source, Integer state, String message);

}
