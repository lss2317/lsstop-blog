package com.lsstop.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志VO
 *
 * @author lishusheng
 * @date 2026/05/24
 */
@Data
public class OperationLogVO {

    /**
     * 日志编号
     */
    private String logNumber;

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
     * 操作用户ID
     */
    private String userId;

    /**
     * 操作人员昵称
     */
    private String nickname;

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
     * 操作状态：0-成功 1-失败
     */
    private Integer state;

    /**
     * 耗时（毫秒）
     */
    private Integer costTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 请求参数
     */
    private String requestParam;

    /**
     * 返回参数
     */
    private String responseParam;

    /**
     * 错误信息
     */
    private String errorMsg;
}
