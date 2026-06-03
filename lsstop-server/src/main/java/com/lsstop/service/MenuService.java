package com.lsstop.service;

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

}
