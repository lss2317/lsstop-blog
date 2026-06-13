package com.lsstop.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 接口权限管理VO（后台管理专用，包含 parentId、sort 等内部字段）
 *
 * @author lishusheng
 * @date 2026/06/12
 */
@Data
public class ApiPermissionAdminVO {

    /**
     * 接口ID
     */
    private Integer id;

    /**
     * 父级权限ID（0表示顶级）
     */
    private Integer parentId;

    /**
     * 接口路径（目录节点为null），如 /admin/article/list
     */
    private String requestUrl;

    /**
     * 请求方法（目录节点为null）：GET、POST、PUT、DELETE
     */
    private String requestMethod;

    /**
     * 权限描述，如"文章管理"/"文章列表"
     */
    private String description;

    /**
     * 排序，值越小越靠前
     */
    private Integer sort;

    /**
     * 是否启用：0-禁用 1-启用
     */
    private Integer isEnabled;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 子节点
     */
    private List<ApiPermissionAdminVO> children;

}
