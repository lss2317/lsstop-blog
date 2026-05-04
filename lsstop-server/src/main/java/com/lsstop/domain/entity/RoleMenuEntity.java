package com.lsstop.domain.entity;

import com.lsstop.domain.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 角色菜单关联实体
 *
 * @author lishusheng
 * @date 2026/05/04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleMenuEntity implements BaseData {

    /**
     * id
     */
    private Integer id;

    /**
     * 角色ID（关联 blog_role.id）
     */
    private Integer roleId;

    /**
     * 菜单ID（关联 blog_menu.id）
     */
    private Integer menuId;

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
