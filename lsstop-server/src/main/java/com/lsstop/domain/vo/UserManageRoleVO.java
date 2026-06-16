package com.lsstop.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户管理-角色简要VO
 *
 * @author lishusheng
 * @date 2026/06/16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserManageRoleVO {

    /**
     * 用户ID（仅用于查询分组，不序列化）
     */
    @JsonIgnore
    private String userId;

    /**
     * 角色ID
     */
    private Integer id;

    /**
     * 角色名称
     */
    private String roleName;
}
