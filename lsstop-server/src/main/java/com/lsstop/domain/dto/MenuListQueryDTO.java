package com.lsstop.domain.dto;

import lombok.Data;

/**
 * 菜单管理列表查询参数
 *
 * @author lishusheng
 * @date 2026/06/07
 */
@Data
public class MenuListQueryDTO {

    /**
     * 关键词（模糊搜索：名称、路由、权限标识）
     */
    private String keyword;

    /**
     * 菜单类型：directory-目录 menu-菜单 button-按钮 iframe-内嵌 link-外链
     */
    private String menuType;

    /**
     * 是否启用：0-禁用 1-启用
     */
    private Integer isEnabled;

}
