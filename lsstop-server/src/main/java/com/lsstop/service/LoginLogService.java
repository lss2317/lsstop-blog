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
     * 发送认证日志到MQ
     *
     * @param userId          用户ID
     * @param loginType       登录方式
     * @param source          操作来源
     * @param state           操作结果
     * @param actionType      操作类型（1登录 2退出 3注册）
     * @param loginIdentifier 操作标识（邮箱/openId/uid）
     * @param message         操作信息
     */
    void sendLoginLog(String userId, Integer loginType, Integer source, Integer state, Integer actionType, String loginIdentifier, String message);

}
