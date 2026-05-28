package com.lsstop.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 认证日志实体（登录/注册/退出）
 *
 * @author lishusheng
 * @date 2026/01/17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginLogEntity {

    /**
     * id
     */
    private Integer id;

    /**
     * 日志编号
     */
    private String logNumber;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 登录方式 1邮箱 2QQ 3微博，退出登录时为null
     */
    private Integer loginType;

    /**
     * 操作时间
     */
    private LocalDateTime loginTime;

    /**
     * 登录ip
     */
    private String ipAddress;

    /**
     * ip所在地
     */
    private String ipRegion;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 操作来源 0前台 1后台 2非法
     */
    private Integer type;

    /**
     * 操作结果 0成功 1失败
     */
    private Integer state;

    /**
     * 操作类型 1登录 2退出 3注册
     */
    private Integer actionType;

    /**
     * 操作标识（登录时为邮箱/openId/uid，退出时为null）
     */
    private String loginIdentifier;

    /**
     * 操作信息（如：登录成功、注册成功、退出登录）
     */
    private String message;

    /**
     * 删除时间戳，0表示未删除
     */
    private Long deletedAt;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
