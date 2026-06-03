package com.lsstop.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 修改角色菜单权限请求参数
 *
 * @author lishusheng
 * @date 2026/06/03
 */
@Data
public class UpdateRoleMenuDTO {

    /**
     * 角色ID
     */
    @NotNull(message = "角色ID不能为空")
    private Integer roleId;

    /**
     * 菜单ID列表（全量选中的菜单ID，包含目录、菜单、按钮）
     * <p>空列表表示清空该角色的所有菜单权限
     */
    @NotNull(message = "菜单ID列表不能为空")
    private List<Integer> menuIds;
}
