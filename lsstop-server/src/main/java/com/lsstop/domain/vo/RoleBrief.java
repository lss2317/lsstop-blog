package com.lsstop.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色简要信息VO
 *
 * @author lishusheng
 * @date 2026/07/05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleBrief {

    /**
     * 角色ID
     */
    private Integer id;

    /**
     * 角色名称
     */
    private String roleName;
}
