package com.lsstop.domain.entity;

import com.lsstop.domain.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 角色接口权限关联实体
 *
 * @author lishusheng
 * @date 2026/05/31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleApiPermissionEntity implements BaseData {

    /**
     * 关联ID
     */
    private Integer id;

    /**
     * 角色ID，关联 blog_role.id
     */
    private Integer roleId;

    /**
     * 接口权限ID，关联 blog_api_permission.id
     */
    private Integer apiPermissionId;

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
