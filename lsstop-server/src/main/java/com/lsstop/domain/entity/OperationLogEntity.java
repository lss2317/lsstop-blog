package com.lsstop.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 操作日志实体
 *
 * @author lishusheng
 * @date 2026/03/29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationLogEntity {

    /**
     * id
     */
    private Integer id;

    /**
     * 操作用户ID
     */
    private String userId;

    /**
     * 操作模块
     */
    private String module;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * 操作描述
     */
    private String description;

    /**
     * 请求路径
     */
    private String requestUrl;

    /**
     * 请求参数
     */
    private String requestParam;

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
     * 耗时（毫秒）
     */
    private Integer costTime;

    /**
     * 操作状态：0成功 1失败
     */
    private Integer state;

    /**
     * 错误信息
     */
    private String errorMsg;

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
