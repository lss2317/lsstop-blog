package com.lsstop.domain.dto;

import lombok.Data;

/**
 * 接口权限列表查询参数
 *
 * @author lishusheng
 * @date 2026/06/12
 */
@Data
public class ApiPermissionQueryDTO {

    /**
     * 关键词（模糊搜索：描述、接口路径）
     */
    private String keyword;

    /**
     * 请求方法：GET、POST、PUT、DELETE
     */
    private String requestMethod;

    /**
     * 是否启用：0-禁用 1-启用
     */
    private Integer isEnabled;

}
