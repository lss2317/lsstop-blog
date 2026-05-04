package com.lsstop.domain.entity;

import com.lsstop.domain.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 角色实体
 *
 * @author lishusheng
 * @date 2026/05/04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleEntity implements BaseData {

    /**
     * 角色ID
     */
    private Integer id;

    /**
     * 角色编码（如 admin, editor, visitor）
     */
    private String roleCode;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色描述
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
