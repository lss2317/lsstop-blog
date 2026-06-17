package com.lsstop.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色选项VO（下拉选择用）
 *
 * @author lishusheng
 * @date 2026/06/17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleOptionVO {

    /**
     * 角色ID
     */
    private Integer id;

    /**
     * 角色名称
     */
    private String roleName;
}
