package com.lsstop.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 登录日志实体
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
     * 用户ID
     */
    private String userId;

    /**
     * 登录方式
     */
    private Integer loginType;

    /**
     * 登录时间
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
     * 登录来源
     */
    private Integer type;

    /**
     * 登录结果
     */
    private Integer state;

    /**
     * 登录信息
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
