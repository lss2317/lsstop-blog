package com.lsstop.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 修改用户菜单权限请求参数
 *
 * @author lishusheng
 * @date 2026/06/19
 */
@Data
public class UpdateUserMenuDTO {

    /**
     * 用户ID
     */
    @NotBlank(message = "用户ID不能为空")
    private String userId;

    /**
     * 菜单ID列表（全量选中的菜单ID）
     * <p>空列表表示清空该用户的所有个性化菜单权限
     */
    @NotNull(message = "菜单ID列表不能为空")
    private List<Integer> menuIds;
}
