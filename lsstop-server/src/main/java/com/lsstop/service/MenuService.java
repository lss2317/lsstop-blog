package com.lsstop.service;

import com.lsstop.domain.dto.AddMenuDTO;
import com.lsstop.domain.dto.UpdateMenuDTO;
import com.lsstop.domain.vo.MenuAdminVO;
import com.lsstop.domain.vo.MenuPermissionVO;
import com.lsstop.domain.vo.MenuVO;

import java.util.List;
import java.util.Set;

/**
 * 菜单服务接口
 *
 * @author lishusheng
 * @date 2026/05/04
 */
public interface MenuService {

    /**
     * 获取当前用户的菜单树
     *
     * @param userId 用户uid
     * @return 树形菜单列表
     */
    List<MenuVO> getUserMenuTree(String userId);

    /**
     * 获取用户的API权限模式集合
     * <p>返回格式如：POST:/admin/article、DELETE:/admin/article/*
     *
     * @param userId 用户uid
     * @return API权限模式集合
     */
    Set<String> getUserApiPermissions(String userId);

    /**
     * 获取系统所有按钮权限规则
     *
     * @return 全局按钮权限 path 集合
     */
    Set<String> getAllApiPermissions();

    /**
     * 获取全量菜单权限树（用于权限配置弹窗）
     * <p>返回系统所有启用菜单的精简树形结构，包含目录、菜单、按钮三类节点
     *
     * @return 菜单权限树
     */
    List<MenuPermissionVO> getMenuPermissionTree();

    /**
     * 获取全量菜单管理树（后台管理页面用）
     * <p>支持关键词、类型、启用状态过滤
     *
     * @param keyword   关键词（模糊搜索）
     * @param menuType  菜单类型（1-目录 2-菜单 3-按钮 4-内嵌 5-外链）
     * @param isEnabled 是否启用（0-禁用 1-启用）
     * @return 菜单管理树
     */
    List<MenuAdminVO> getAdminMenuTree(String keyword, Integer menuType, Integer isEnabled);

    /**
     * 新增菜单
     *
     * @param dto 新增菜单参数
     */
    void addMenu(AddMenuDTO dto);

    /**
     * 删除菜单（软删除）
     *
     * @param id 菜单ID
     */
    void deleteMenu(Integer id);

    /**
     * 编辑菜单
     *
     * @param dto 编辑菜单参数
     */
    void updateMenu(UpdateMenuDTO dto);

}
