package com.lsstop.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

/**
 * 菜单权限树节点VO（精简版，仅用于权限配置弹窗）
 *
 * @author lishusheng
 * @date 2026/06/03
 */
@Data
public class MenuPermissionVO {

    /**
     * 菜单ID
     */
    private Integer id;

    /**
     * 父级ID（0=顶级）
     */
    @JsonIgnore
    private Integer parentId;

    /**
     * 路由name（唯一标识，作为 tree node-key）
     */
    private String name;

    /**
     * 菜单标题
     */
    private String title;

    /**
     * 子节点
     */
    private List<MenuPermissionVO> children;

}
