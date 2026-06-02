package com.lsstop.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 编辑角色请求参数
 *
 * @author lishusheng
 * @date 2026/06/02
 */
@Data
public class UpdateRoleDTO {

    /**
     * 角色ID
     */
    @NotNull(message = "角色ID不能为空")
    private Integer id;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 20, message = "角色名称不能超过20个字符")
    private String roleName;

    /**
     * 角色编码
     */
    @NotBlank(message = "角色编码不能为空")
    @Size(max = 20, message = "角色编码不能超过20个字符")
    private String roleCode;

    /**
     * 角色描述
     */
    @NotBlank(message = "角色描述不能为空")
    @Size(max = 100, message = "角色描述不能超过100个字符")
    private String description;

    /**
     * 是否启用：0-禁用 1-启用
     */
    @NotNull(message = "启用状态不能为空")
    private Integer isEnabled;
}
