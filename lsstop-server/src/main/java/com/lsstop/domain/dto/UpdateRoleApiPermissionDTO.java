package com.lsstop.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 修改角色接口权限请求参数
 *
 * @author lishusheng
 * @date 2026/06/04
 */
@Data
public class UpdateRoleApiPermissionDTO {

    /**
     * 角色ID
     */
    @NotNull(message = "角色ID不能为空")
    private Integer roleId;

    /**
     * 接口权限ID列表（全量选中的接口权限ID，包含目录和接口）
     * <p>空列表表示清空该角色的所有接口权限
     */
    @NotNull(message = "接口权限ID列表不能为空")
    private List<Integer> apiIds;
}
