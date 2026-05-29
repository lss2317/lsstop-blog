package com.lsstop.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 认证日志VO
 *
 * @author lishusheng
 * @date 2026/05/29
 */
@Data
public class LoginLogVO {

    /**
     * 日志编号
     */
    private String logNumber;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 登录方式：1-邮箱 2-QQ 3-微博
     */
    private Integer loginType;

    /**
     * 操作时间
     */
    private LocalDateTime loginTime;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * IP归属地
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
     * 操作来源：0-前台 1-后台 2-非法
     */
    private Integer type;

    /**
     * 操作结果：0-成功 1-失败
     */
    private Integer state;

    /**
     * 操作类型：1-登录 2-退出 3-注册
     */
    private Integer actionType;

    /**
     * 操作标识（登录时为邮箱/openId/uid，退出时为null）
     */
    private String loginIdentifier;

    /**
     * 操作信息
     */
    private String message;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
