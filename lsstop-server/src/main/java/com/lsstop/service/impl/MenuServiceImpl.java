package com.lsstop.service.impl;

import com.lsstop.constant.RedisConst;
import com.lsstop.domain.vo.MenuAdminVO;
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
import java.util.function.BiConsumer;
import java.util.function.Function;
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
        List<MenuVO> menuTree = buildUserMenuTree(flatMenus);
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
        List<MenuPermissionVO> tree = buildPermissionMenuTree(flatMenus);
        // 写入缓存，过期时间1天
        redisUtils.set(cacheKey, tree, RedisConst.EXPIRE_ONE_DAY);
        return tree;
    }

    /**
     * 通用树形构建方法
     * <p>将扁平列表构建为树形结构，支持任意具有 id、parentId、children 的 VO
     *
     * @param flatList    扁平列表（已按 sort, id 排序）
     * @param getId       获取节点 ID
     * @param getParentId 获取父节点 ID
     * @param getChildren 获取子节点列表
     * @param setChildren 设置子节点列表
     * @return 树形结构列表
     */
    private <T> List<T> buildTreeFromFlatList(
            List<T> flatList,
            Function<T, Integer> getId,
            Function<T, Integer> getParentId,
            Function<T, List<T>> getChildren,
            BiConsumer<T, List<T>> setChildren) {

        if (flatList == null || flatList.isEmpty()) {
            return new ArrayList<>();
        }

        // 使用 LinkedHashMap 保持插入顺序（即排序顺序）
        Map<Integer, T> nodeMap = new LinkedHashMap<>();
        for (T node : flatList) {
            setChildren.accept(node, new ArrayList<>());
            nodeMap.put(getId.apply(node), node);
        }

        List<T> roots = new ArrayList<>();
        for (T node : flatList) {
            Integer parentId = getParentId.apply(node);
            if (parentId == null || parentId == 0) {
                roots.add(node);
            } else {
                T parent = nodeMap.get(parentId);
                if (parent != null) {
                    getChildren.apply(parent).add(node);
                }
                // 父节点不在列表内，直接丢弃
            }
        }

        // 清理空的 children 列表，设为 null
        clearEmptyChildren(roots, getChildren, setChildren);
        return roots;
    }

    /**
     * 递归清理空的 children，设为 null
     */
    private <T> void clearEmptyChildren(
            List<T> nodes,
            Function<T, List<T>> getChildren,
            BiConsumer<T, List<T>> setChildren) {
        for (T node : nodes) {
            List<T> children = getChildren.apply(node);
            if (children != null && children.isEmpty()) {
                setChildren.accept(node, null);
            } else if (children != null) {
                clearEmptyChildren(children, getChildren, setChildren);
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
     * 补全管理后台菜单列表中缺失的父级目录
     * <p>当子菜单在搜索命中但父级目录不在时，向上递归补全所有祖先，
     * 确保子菜单在构建树结构时不会被丢弃
     *
     * @param flatMenus 原始扁平菜单列表
     * @return 补全后的扁平菜单列表
     */
    private List<MenuAdminVO> fillMissingAdminParents(List<MenuAdminVO> flatMenus) {
        if (flatMenus.isEmpty()) {
            return flatMenus;
        }
        List<MenuAdminVO> result = new ArrayList<>(flatMenus);
        Set<Integer> existingIds = result.stream()
                .map(MenuAdminVO::getId)
                .collect(Collectors.toSet());

        // 收集所有缺失的父级ID
        Set<Integer> missingIds = result.stream()
                .map(MenuAdminVO::getParentId)
                .filter(p -> p != null && p != 0)
                .filter(p -> !existingIds.contains(p))
                .collect(Collectors.toSet());

        // 向上递归补全所有祖先
        while (!missingIds.isEmpty()) {
            List<MenuAdminVO> parents = menuMapper.selectAdminMenusByIds(new ArrayList<>(missingIds));
            if (parents.isEmpty()) {
                break;
            }
            result.addAll(parents);
            for (MenuAdminVO parent : parents) {
                existingIds.add(parent.getId());
            }
            missingIds.clear();
            for (MenuAdminVO parent : parents) {
                if (parent.getParentId() != null && parent.getParentId() != 0
                        && !existingIds.contains(parent.getParentId())) {
                    missingIds.add(parent.getParentId());
                }
            }
        }
        return result;
    }

    /**
     * 将扁平菜单列表构建为用户菜单树（侧边栏）
     *
     * @param flatMenus 扁平菜单列表（已按 sort, id 排序）
     * @return 树形菜单列表
     */
    private List<MenuVO> buildUserMenuTree(List<MenuVO> flatMenus) {
        return buildTreeFromFlatList(flatMenus,
                MenuVO::getId, MenuVO::getParentId,
                MenuVO::getChildren, MenuVO::setChildren);
    }

    /**
     * 获取全量菜单管理树（后台管理页面用）
     * <p>支持关键词、类型、启用状态过滤
     *
     * @param keyword   关键词（模糊搜索）
     * @param menuType  菜单类型（1-目录 2-菜单 3-按钮 4-内嵌 5-外链）
     * @param isEnabled 是否启用（0-禁用 1-启用）
     * @return 菜单管理树
     */
    @Override
    public List<MenuAdminVO> getAdminMenuTree(String keyword, Integer menuType, Integer isEnabled) {
        List<MenuAdminVO> flatMenus = menuMapper.selectAllMenus(keyword, menuType, isEnabled);
        // 补全缺失的父级目录，确保搜索命中的子菜单不被丢弃
        flatMenus = fillMissingAdminParents(flatMenus);
        return buildAdminMenuTree(flatMenus);
    }

    /**
     * 将扁平菜单列表构建为管理后台树形结构
     *
     * @param flatMenus 扁平菜单列表（已按 sort, id 排序）
     * @return 树形菜单列表
     */
    private List<MenuAdminVO> buildAdminMenuTree(List<MenuAdminVO> flatMenus) {
        return buildTreeFromFlatList(flatMenus,
                MenuAdminVO::getId, MenuAdminVO::getParentId,
                MenuAdminVO::getChildren, MenuAdminVO::setChildren);
    }

    /**
     * 将精简菜单列表构建为权限树（权限配置弹窗专用）
     *
     * @param flatMenus 扁平菜单列表（已按 sort, id 排序）
     * @return 树形菜单列表
     */
    private List<MenuPermissionVO> buildPermissionMenuTree(List<MenuPermissionVO> flatMenus) {
        return buildTreeFromFlatList(flatMenus,
                MenuPermissionVO::getId, MenuPermissionVO::getParentId,
                MenuPermissionVO::getChildren, MenuPermissionVO::setChildren);
    }

}
