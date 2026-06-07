package com.lsstop.domain.entity;

import com.lsstop.domain.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 接口权限定义实体
 *
 * @author lishusheng
 * @date 2026/05/31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiPermissionEntity implements BaseData {

    /**
     * 权限ID
     */
    private Integer id;

    /**
     * 父级权限ID，0表示顶级
     */
    private Integer parentId;

    /**
     * 权限标识（如 add）
     */
    private String module;

    /**
     * 接口路径（目录节点为NULL），如 /admin/article/list
     */
    private String requestUrl;

    /**
     * 请求方法（目录节点为NULL）：GET、POST、PUT、DELETE
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
