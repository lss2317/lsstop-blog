package com.lsstop.service.impl;

import com.lsstop.constant.RedisConst;
import com.lsstop.domain.vo.MenuPermissionVO;
import com.lsstop.domain.vo.MenuVO;
import com.lsstop.mapper.MenuMapper;
import com.lsstop.service.MenuService;
import com.lsstop.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 菜单服务实现类
 *
 * @author lishusheng
 * @date 2026/05/04
 */
@Service
public class MenuServiceImpl implements MenuService {

    @Resource
    private MenuMapper menuMapper;

    @Resource
    private RedisUtils redisUtils;

    /**
     * 获取当前用户的菜单树
     *
     * @param userId 用户uid
     * @return 树形菜单列表
     */
    @Override
    public List<MenuVO> getUserMenuTree(String userId) {
        String cacheKey = RedisConst.USER_MENU_TREE + userId;
        // 先从缓存获取
        List<MenuVO> cachedMenus = redisUtils.getList(cacheKey, MenuVO.class);
        if (cachedMenus != null) {
            return cachedMenus;
        }
        // 缓存不存在，查询数据库
        List<MenuVO> flatMenus = menuMapper.selectMenusByUserId(userId);
        // 补全缺失的父级目录，确保子菜单在 buildTree 中不被丢弃
        flatMenus = fillMissingParents(flatMenus);
        List<MenuVO> menuTree = buildTree(flatMenus);
        // 写入缓存，过期时间1天
        redisUtils.set(cacheKey, menuTree, RedisConst.EXPIRE_ONE_DAY);
        return menuTree;
    }

    /**
     * 获取用户的API权限模式集合
     * <p>从menuType=3的按钮权限中提取path字段，格式：METHOD:/uri/pattern
     *
     * @param userId 用户uid
     * @return API权限模式集合
     */
    @Override
    public Set<String> getUserApiPermissions(String userId) {
        String cacheKey = RedisConst.USER_API_PERMISSIONS + userId;
        // 先从缓存获取
        Set<String> cachedPermissions = redisUtils.getSet(cacheKey, String.class);
        if (cachedPermissions != null) {
            return cachedPermissions;
        }
        // 缓存不存在，从菜单树中提取
        List<MenuVO> flatMenus = menuMapper.selectMenusByUserId(userId);
        Set<String> permissions = new HashSet<>();
        for (MenuVO menu : flatMenus) {
            // menuType=3 为按钮权限，path 存储 API 路径模式
            if (menu.getMenuType() != null && menu.getMenuType() == 3
                    && menu.getPath() != null && !menu.getPath().isBlank()) {
                permissions.add(menu.getPath());
            }
        }
        // 写入缓存，过期时间1天
        redisUtils.set(cacheKey, permissions, RedisConst.EXPIRE_ONE_DAY);
        return permissions;
    }

    /**
     * 获取系统所有按钮权限规则
     *
     * @return 全局按钮权限 path 集合
     */
    @Override
    public Set<String> getAllApiPermissions() {
        String cacheKey = RedisConst.ALL_API_PERMISSIONS;
        // 先从缓存获取
        Set<String> cachedPermissions = redisUtils.getSet(cacheKey, String.class);
        if (cachedPermissions != null) {
            return cachedPermissions;
        }
        // 缓存不存在，查询数据库
        List<String> paths = menuMapper.selectAllButtonPaths();
        Set<String> permissions = new HashSet<>(paths);
        // 写入缓存，过期时间1天
        redisUtils.set(cacheKey, permissions, RedisConst.EXPIRE_ONE_DAY);
        return permissions;
    }

    /**
     * 获取全量菜单权限树（用于权限配置弹窗）
     * <p>返回系统所有启用菜单的精简树形结构，包含目录、菜单、按钮三类节点
     *
     * @return 菜单权限树
     */
    @Override
    public List<MenuPermissionVO> getMenuPermissionTree() {
        String cacheKey = RedisConst.MENU_PERMISSION_TREE;
        // 先从缓存获取
        List<MenuPermissionVO> cachedTree = redisUtils.getList(cacheKey, MenuPermissionVO.class);
        if (cachedTree != null) {
            return cachedTree;
        }
        // 缓存不存在，查询数据库
        List<MenuPermissionVO> flatMenus = menuMapper.selectAllMenusForPermission();
        List<MenuPermissionVO> tree = buildPermissionTree(flatMenus);
        // 写入缓存，过期时间1天
        redisUtils.set(cacheKey, tree, RedisConst.EXPIRE_ONE_DAY);
        return tree;
    }

    /**
     * 将精简菜单列表构建为树形结构（权限配置弹窗专用）
     *
     * @param flatMenus 扁平菜单列表（已按 sort, id 排序）
     * @return 树形菜单列表
     */
    private List<MenuPermissionVO> buildPermissionTree(List<MenuPermissionVO> flatMenus) {
        // 使用 LinkedHashMap 保持插入顺序（即排序顺序）
        Map<Integer, MenuPermissionVO> menuMap = new LinkedHashMap<>();
        for (MenuPermissionVO menu : flatMenus) {
            menu.setChildren(new ArrayList<>());
            menuMap.put(menu.getId(), menu);
        }

        List<MenuPermissionVO> rootMenus = new ArrayList<>();
        for (MenuPermissionVO menu : flatMenus) {
            if (menu.getParentId() == null || menu.getParentId() == 0) {
                rootMenus.add(menu);
            } else {
                MenuPermissionVO parent = menuMap.get(menu.getParentId());
                if (parent != null) {
                    parent.getChildren().add(menu);
                }
                // 父节点不在启用范围内，直接丢弃
            }
        }

        // 清理空的 children 列表，设为 null
        removeEmptyPermissionChildren(rootMenus);
        return rootMenus;
    }

    /**
     * 递归清理空的 children，设为 null（权限树专用）
     */
    private void removeEmptyPermissionChildren(List<MenuPermissionVO> menus) {
        for (MenuPermissionVO menu : menus) {
            if (menu.getChildren() != null && menu.getChildren().isEmpty()) {
                menu.setChildren(null);
            } else if (menu.getChildren() != null) {
                removeEmptyPermissionChildren(menu.getChildren());
            }
        }
    }

    /**
     * 补全菜单列表中缺失的父级目录
     * <p>当子菜单在权限列表中但父级目录不在时，向上递归补全所有祖先目录，
     * 确保子菜单在构建树结构时不会被丢弃
     *
     * @param flatMenus 原始扁平菜单列表
     * @return 补全后的扁平菜单列表
     */
    private List<MenuVO> fillMissingParents(List<MenuVO> flatMenus) {
        if (flatMenus.isEmpty()) {
            return flatMenus;
        }
        List<MenuVO> result = new ArrayList<>(flatMenus);
        Set<Integer> existingIds = result.stream()
                .map(MenuVO::getId)
                .collect(Collectors.toSet());

        // 收集所有缺失的父级ID
        Set<Integer> missingIds = result.stream()
                .map(MenuVO::getParentId)
                .filter(p -> p != null && p != 0)
                .filter(p -> !existingIds.contains(p))
                .collect(Collectors.toSet());

        // 向上递归补全所有祖先
        while (!missingIds.isEmpty()) {
            List<MenuVO> parents = menuMapper.selectMenusByIds(new ArrayList<>(missingIds));
            if (parents.isEmpty()) {
                break;
            }
            result.addAll(parents);
            for (MenuVO parent : parents) {
                existingIds.add(parent.getId());
            }
            missingIds.clear();
            for (MenuVO parent : parents) {
                if (parent.getParentId() != null && parent.getParentId() != 0
                        && !existingIds.contains(parent.getParentId())) {
                    missingIds.add(parent.getParentId());
                }
            }
        }
        return result;
    }

    /**
     * 将扁平菜单列表构建为树形结构
     *
     * @param flatMenus 扁平菜单列表（已按 sort, id 排序）
     * @return 树形菜单列表
     */
    private List<MenuVO> buildTree(List<MenuVO> flatMenus) {
        // 使用 LinkedHashMap 保持插入顺序（即排序顺序）
        Map<Integer, MenuVO> menuMap = new LinkedHashMap<>();
        for (MenuVO menu : flatMenus) {
            menu.setChildren(new ArrayList<>());
            menuMap.put(menu.getId(), menu);
        }

        List<MenuVO> rootMenus = new ArrayList<>();
        for (MenuVO menu : flatMenus) {
            if (menu.getParentId() == null || menu.getParentId() == 0) {
                rootMenus.add(menu);
            } else {
                MenuVO parent = menuMap.get(menu.getParentId());
                if (parent != null) {
                    parent.getChildren().add(menu);
                }
                // 父节点不在权限范围内，直接丢弃（无入口可达）
            }
        }

        // 清理空的 children 列表，设为 null
        removeEmptyChildren(rootMenus);
        return rootMenus;
    }

    /**
     * 递归清理空的 children，设为 null
     */
    private void removeEmptyChildren(List<MenuVO> menus) {
        for (MenuVO menu : menus) {
            if (menu.getChildren() != null && menu.getChildren().isEmpty()) {
                menu.setChildren(null);
            } else if (menu.getChildren() != null) {
                removeEmptyChildren(menu.getChildren());
            }
        }
    }

}
