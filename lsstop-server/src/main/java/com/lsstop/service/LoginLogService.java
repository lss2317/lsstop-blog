package com.lsstop.service;

import com.lsstop.domain.entity.LoginLogEntity;
import com.lsstop.domain.vo.LoginLogVO;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

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

    /**
     * 分页查询认证日志列表
     *
     * @param current    当前页码
     * @param pageSize   每页数量
     * @param userId     用户ID
     * @param actionType 操作类型：1-登录 2-退出 3-注册
     * @param state      操作结果：0-成功 1-失败
     * @param type       操作来源：0-前台 1-后台 2-非法
     * @param loginType  登录方式：1-邮箱 2-QQ 3-微博
     * @param noUserId   仅筛无用户ID的记录
     * @return 认证日志列表
     */
    List<LoginLogVO> listLoginLogs(Integer current, Integer pageSize,
                                   String userId, Integer actionType,
                                   Integer state, Integer type,
                                   Integer loginType, Boolean noUserId);

    /**
     * 统计认证日志总数
     *
     * @param userId     用户ID
     * @param actionType 操作类型：1-登录 2-退出 3-注册
     * @param state      操作结果：0-成功 1-失败
     * @param type       操作来源：0-前台 1-后台 2-非法
     * @param loginType  登录方式：1-邮箱 2-QQ 3-微博
     * @param noUserId   仅筛无用户ID的记录
     * @return 认证日志总数
     */
    Integer countLoginLogTotal(String userId, Integer actionType,
                               Integer state, Integer type,
                               Integer loginType, Boolean noUserId);

    /**
     * 删除认证日志（支持单个和批量删除）
     *
     * @param logNumbers 日志编号列表
     */
    void deleteLoginLogs(List<String> logNumbers);

    /**
     * 导出认证日志为 Excel
     *
     * @param userId     用户ID
     * @param actionType 操作类型：1-登录 2-退出 3-注册
     * @param state      操作结果：0-成功 1-失败
     * @param type       操作来源：0-前台 1-后台 2-非法
     * @param loginType  登录方式：1-邮箱 2-QQ 3-微博
     * @param noUserId   仅筛无用户ID的记录
     * @param response   HTTP响应，用于写出Excel文件
     */
    void exportLoginLogs(String userId, Integer actionType, Integer state, Integer type,
                         Integer loginType, Boolean noUserId,
                         HttpServletResponse response) throws IOException;

}
