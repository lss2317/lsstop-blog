package com.lsstop.service.impl;

import com.lsstop.constant.CommonConst;
import com.lsstop.constant.MenuConst;
import com.lsstop.constant.RedisConst;
import com.lsstop.domain.dto.AddMenuDTO;
import com.lsstop.domain.entity.MenuEntity;
import com.lsstop.domain.vo.MenuAdminVO;
import com.lsstop.domain.vo.MenuPermissionVO;
import com.lsstop.domain.vo.MenuVO;
import com.lsstop.enums.MenuTypeEnum;
import com.lsstop.enums.StatusEnum;
import com.lsstop.exception.BusinessException;
import com.lsstop.mapper.MenuMapper;
import com.lsstop.service.MenuService;
import com.lsstop.utils.ConvertUtils;
import com.lsstop.utils.StringUtils;
import com.lsstop.utils.ValidateUtils;
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

    /**
     * 新增菜单
     *
     * @param dto 新增菜单参数
     */
    @Override
    public void addMenu(AddMenuDTO dto) {
        // title 去首尾空格
        dto.setTitle(dto.getTitle().trim());

        // 1. 校验菜单类型有效性
        Integer menuTypeCode = MenuTypeEnum.toCode(dto.getMenuType());
        if (menuTypeCode == null) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), MenuConst.INVALID_MENU_TYPE);
        }

        int parentId = dto.getParentId() != null ? dto.getParentId() : MenuConst.TOP_LEVEL_PARENT_ID;

        // 2. 分支校验 + 关系校验
        switch (menuTypeCode) {
            case MenuConst.TYPE_DIRECTORY -> validateDirectory(dto, parentId);
            case MenuConst.TYPE_MENU -> validateMenu(dto, parentId);
            case MenuConst.TYPE_BUTTON -> validateButton(dto, parentId);
            case MenuConst.TYPE_IFRAME -> validateIframe(dto, parentId);
            case MenuConst.TYPE_LINK -> validateLink(dto, parentId);
            default -> throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), MenuConst.INVALID_MENU_TYPE);
        }

        // 3. 唯一性校验
        validateUniqueness(dto, parentId, menuTypeCode);

        // 4. 构建实体（按类型忽略无关字段）
        MenuEntity entity = buildMenuEntity(dto, parentId, menuTypeCode);
        menuMapper.insertMenu(entity);

        // 5. 清理Redis缓存
        clearMenuCache(menuTypeCode);
    }

    /**
     * 删除菜单（软删除）
     * <p>校验菜单是否存在、是否有子菜单，软删除后清理相关缓存
     *
     * @param id 菜单ID
     */
    @Override
    public void deleteMenu(Integer id) {
        // 1. 校验菜单是否存在
        MenuEntity menu = menuMapper.selectMenuById(id);
        if (menu == null) {
            throw new BusinessException(StatusEnum.NOT_FOUND, MenuConst.MENU_NOT_FOUND);
        }

        // 2. 校验是否存在子菜单
        Integer childCount = menuMapper.countByParentId(id);
        if (childCount != null && childCount > 0) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), MenuConst.HAS_CHILDREN);
        }

        // 3. 执行软删除
        menuMapper.deleteById(id, System.currentTimeMillis());

        // 4. 清理Redis缓存
        clearMenuCache(menu.getMenuType());
    }

    /**
     * 清理菜单相关Redis缓存
     * <p>新增菜单后，清除受影响的缓存，下次查询时自动重建
     *
     * @param menuTypeCode 菜单类型编码
     */
    private void clearMenuCache(int menuTypeCode) {
        // 全量菜单权限树缓存（任何菜单变更都影响）
        redisUtils.delete(RedisConst.MENU_PERMISSION_TREE);
        // 全量接口权限树缓存（任何菜单变更都影响）
        redisUtils.delete(RedisConst.API_PERMISSION_TREE);
        // 按用户的菜单树缓存（不确定影响哪些用户，全部清除）
        redisUtils.deleteByPrefix(RedisConst.USER_MENU_TREE);

        if (menuTypeCode == MenuConst.TYPE_BUTTON) {
            // 全局按钮权限规则缓存
            redisUtils.delete(RedisConst.ALL_API_PERMISSIONS);
            // 按用户的API权限缓存
            redisUtils.deleteByPrefix(RedisConst.USER_API_PERMISSIONS);
        }
    }

    /**
     * 校验目录类型
     */
    private void validateDirectory(AddMenuDTO dto, int parentId) {
        // path 必填 + 格式
        if (StringUtils.isBlank(dto.getPath())) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), MenuConst.PATH_REQUIRED);
        }
        validatePathFormat(dto.getPath(), parentId);
        // name 格式（选填）
        if (!StringUtils.isBlank(dto.getName())) {
            validateNameFormat(dto.getName());
        }
        // icon 格式（选填）
        validateIconFormat(dto.getIcon());
        // 父级校验：非顶级时父级必须是目录
        if (parentId != MenuConst.TOP_LEVEL_PARENT_ID) {
            validateParentType(parentId, MenuConst.TYPE_DIRECTORY);
        }
    }

    /**
     * 校验菜单类型
     */
    private void validateMenu(AddMenuDTO dto, int parentId) {
        // path 必填 + 格式
        if (StringUtils.isBlank(dto.getPath())) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), MenuConst.PATH_REQUIRED);
        }
        validatePathFormat(dto.getPath(), parentId);
        // component 必填 + 格式
        if (StringUtils.isBlank(dto.getComponent())) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), MenuConst.COMPONENT_REQUIRED);
        }
        validateComponentFormat(dto.getComponent());
        // name 格式（选填）
        if (!StringUtils.isBlank(dto.getName())) {
            validateNameFormat(dto.getName());
        }
        // activePath 格式（选填）
        if (!StringUtils.isBlank(dto.getActivePath())) {
            validateActivePathFormat(dto.getActivePath());
        }
        // icon 格式（选填）
        validateIconFormat(dto.getIcon());
        // 父级校验：非顶级时父级必须是目录或菜单
        if (parentId != MenuConst.TOP_LEVEL_PARENT_ID) {
            validateParentType(parentId, MenuConst.TYPE_DIRECTORY, MenuConst.TYPE_MENU);
        }
    }

    /**
     * 校验按钮类型
     */
    private void validateButton(AddMenuDTO dto, int parentId) {
        // parentId 必填
        if (parentId == MenuConst.TOP_LEVEL_PARENT_ID) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), MenuConst.BUTTON_PARENT_REQUIRED);
        }
        // authMark 必填 + 格式
        if (StringUtils.isBlank(dto.getAuthMark())) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), MenuConst.AUTH_MARK_REQUIRED);
        }
        validateAuthMarkFormat(dto.getAuthMark());
        // 父级必须是菜单
        validateParentType(parentId, MenuConst.TYPE_MENU);
    }

    /**
     * 校验内嵌类型
     */
    private void validateIframe(AddMenuDTO dto, int parentId) {
        // path 必填 + 格式
        if (StringUtils.isBlank(dto.getPath())) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), MenuConst.PATH_REQUIRED);
        }
        validatePathFormat(dto.getPath(), parentId);
        // link 必填且格式正确
        validateLinkUrl(dto.getLink());
        // name 格式（选填）
        if (!StringUtils.isBlank(dto.getName())) {
            validateNameFormat(dto.getName());
        }
        // icon 格式（选填）
        validateIconFormat(dto.getIcon());
        // 父级校验：非顶级时父级必须是目录或菜单
        if (parentId != MenuConst.TOP_LEVEL_PARENT_ID) {
            validateParentType(parentId, MenuConst.TYPE_DIRECTORY, MenuConst.TYPE_MENU);
        }
    }

    /**
     * 校验外链类型
     */
    private void validateLink(AddMenuDTO dto, int parentId) {
        // link 必填且格式正确
        validateLinkUrl(dto.getLink());
        // name 格式（选填）
        if (!StringUtils.isBlank(dto.getName())) {
            validateNameFormat(dto.getName());
        }
        // icon 格式（选填）
        validateIconFormat(dto.getIcon());
        // 父级校验：非顶级时父级必须是目录或菜单
        if (parentId != MenuConst.TOP_LEVEL_PARENT_ID) {
            validateParentType(parentId, MenuConst.TYPE_DIRECTORY, MenuConst.TYPE_MENU);
        }
    }

    /**
     * 校验父级菜单类型
     */
    private void validateParentType(int parentId, int... expectedTypes) {
        MenuEntity parent = menuMapper.selectMenuById(parentId);
        if (parent == null) {
            throw new BusinessException(StatusEnum.NOT_FOUND.getCode(), MenuConst.PARENT_NOT_FOUND);
        }
        for (int expectedType : expectedTypes) {
            if (parent.getMenuType() == expectedType) {
                return;
            }
        }
        // 根据传入类型确定错误信息
        String msg;
        if (expectedTypes.length == 1 && expectedTypes[0] == MenuConst.TYPE_MENU) {
            msg = MenuConst.BUTTON_MUST_UNDER_MENU;
        } else if (expectedTypes.length == 1 && expectedTypes[0] == MenuConst.TYPE_DIRECTORY) {
            msg = MenuConst.MUST_UNDER_DIRECTORY;
        } else {
            msg = MenuConst.MUST_UNDER_DIRECTORY_OR_MENU;
        }
        throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), msg);
    }

    /**
     * 校验链接URL格式
     */
    private void validateLinkUrl(String link) {
        if (StringUtils.isBlank(link)) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), MenuConst.LINK_REQUIRED);
        }
        if (link.length() > MenuConst.LINK_MAX_LENGTH) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), MenuConst.LINK_INVALID);
        }
        if (!StringUtils.isValidUrl(link)) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), MenuConst.LINK_INVALID);
        }
    }

    /**
     * 唯一性校验
     */
    private void validateUniqueness(AddMenuDTO dto, int parentId, int menuTypeCode) {
        // name 全局唯一（非空时校验）
        if (!StringUtils.isBlank(dto.getName())) {
            Integer nameCount = menuMapper.countByName(dto.getName());
            if (nameCount != null && nameCount > 0) {
                throw new BusinessException(StatusEnum.USERNAME_OR_EMAIL_EXIST.getCode(), MenuConst.NAME_EXISTS);
            }
        }
        // path 同级唯一（非按钮、非外链、path非空时校验）
        if (menuTypeCode != MenuConst.TYPE_BUTTON && menuTypeCode != MenuConst.TYPE_LINK
                && !StringUtils.isBlank(dto.getPath())) {
            Integer pathCount = menuMapper.countByParentIdAndPath(parentId, dto.getPath());
            if (pathCount != null && pathCount > 0) {
                throw new BusinessException(StatusEnum.USERNAME_OR_EMAIL_EXIST.getCode(), MenuConst.PATH_EXISTS);
            }
        }
        // authMark 同级唯一（按钮类型、非空时校验）
        if (menuTypeCode == MenuConst.TYPE_BUTTON && !StringUtils.isBlank(dto.getAuthMark())) {
            Integer authMarkCount = menuMapper.countByParentIdAndAuthMark(parentId, dto.getAuthMark());
            if (authMarkCount != null && authMarkCount > 0) {
                throw new BusinessException(StatusEnum.USERNAME_OR_EMAIL_EXIST.getCode(), MenuConst.AUTH_MARK_EXISTS);
            }
        }
    }

    /**
     * 构建菜单实体（按类型忽略无关字段）
     */
    private MenuEntity buildMenuEntity(AddMenuDTO dto, int parentId, int menuTypeCode) {
        MenuEntity.MenuEntityBuilder builder = MenuEntity.builder()
                .parentId(parentId)
                .menuType(menuTypeCode)
                .title(dto.getTitle())
                .sort(dto.getSort() != null ? dto.getSort() : MenuConst.DEFAULT_SORT)
                .isEnabled(ConvertUtils.boolToInt(dto.getIsEnabled(), true));

        switch (menuTypeCode) {
            case MenuConst.TYPE_DIRECTORY -> builder
                    .name(StringUtils.isBlank(dto.getName()) ? null : dto.getName())
                    .path(dto.getPath())
                    .icon(dto.getIcon())
                    .isHide(ConvertUtils.boolToInt(dto.getIsHide()))
                    .isHideTab(ConvertUtils.boolToInt(dto.getIsHideTab()))
                    .keepAlive(CommonConst.DISABLED)
                    .isFullPage(ConvertUtils.boolToInt(dto.getIsFullPage()))
                    .isFirstLevel(CommonConst.DISABLED)
                    .fixedTab(ConvertUtils.boolToInt(dto.getFixedTab()))
                    .isIframe(CommonConst.DISABLED);
            case MenuConst.TYPE_MENU -> builder
                    .name(StringUtils.isBlank(dto.getName()) ? null : dto.getName())
                    .path(dto.getPath())
                    .component(dto.getComponent())
                    .icon(dto.getIcon())
                    .activePath(dto.getActivePath())
                    .isHide(ConvertUtils.boolToInt(dto.getIsHide()))
                    .isHideTab(ConvertUtils.boolToInt(dto.getIsHideTab()))
                    .keepAlive(ConvertUtils.boolToInt(dto.getKeepAlive(), true))
                    .isFullPage(ConvertUtils.boolToInt(dto.getIsFullPage()))
                    .isFirstLevel(CommonConst.DISABLED)
                    .fixedTab(ConvertUtils.boolToInt(dto.getFixedTab()))
                    .isIframe(CommonConst.DISABLED);
            case MenuConst.TYPE_BUTTON -> builder
                    .authMark(dto.getAuthMark())
                    .isHide(CommonConst.DISABLED)
                    .isHideTab(CommonConst.DISABLED)
                    .keepAlive(CommonConst.DISABLED)
                    .isFullPage(CommonConst.DISABLED)
                    .isFirstLevel(CommonConst.DISABLED)
                    .fixedTab(CommonConst.DISABLED)
                    .isIframe(CommonConst.DISABLED);
            case MenuConst.TYPE_IFRAME -> builder
                    .name(StringUtils.isBlank(dto.getName()) ? null : dto.getName())
                    .path(dto.getPath())
                    .link(dto.getLink())
                    .icon(dto.getIcon())
                    .isHide(ConvertUtils.boolToInt(dto.getIsHide()))
                    .isHideTab(ConvertUtils.boolToInt(dto.getIsHideTab()))
                    .keepAlive(CommonConst.DISABLED)
                    .isFullPage(ConvertUtils.boolToInt(dto.getIsFullPage()))
                    .isFirstLevel(CommonConst.DISABLED)
                    .fixedTab(ConvertUtils.boolToInt(dto.getFixedTab()))
                    .isIframe(CommonConst.ENABLED);
            case MenuConst.TYPE_LINK -> builder
                    .name(StringUtils.isBlank(dto.getName()) ? null : dto.getName())
                    .link(dto.getLink())
                    .icon(dto.getIcon())
                    .isHide(ConvertUtils.boolToInt(dto.getIsHide()))
                    .isHideTab(ConvertUtils.boolToInt(dto.getIsHideTab()))
                    .keepAlive(CommonConst.DISABLED)
                    .isFullPage(ConvertUtils.boolToInt(dto.getIsFullPage()))
                    .isFirstLevel(CommonConst.DISABLED)
                    .fixedTab(ConvertUtils.boolToInt(dto.getFixedTab()))
                    .isIframe(CommonConst.DISABLED);
            default -> {}
        }
        return builder.build();
    }

    /**
     * 校验path格式
     */
    private void validatePathFormat(String path, int parentId) {
        if (path.length() > MenuConst.PATH_MAX_LENGTH) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), MenuConst.PATH_LENGTH_INVALID);
        }
        // 顶级必须以/开头
        if (parentId == MenuConst.TOP_LEVEL_PARENT_ID && !path.startsWith("/")) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), MenuConst.PATH_TOP_LEVEL_SLASH);
        }
        // 非顶级不能以/开头
        if (parentId != MenuConst.TOP_LEVEL_PARENT_ID && path.startsWith("/")) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), MenuConst.PATH_SUB_LEVEL_NO_SLASH);
        }
        if (!ValidateUtils.isValidPath(path)) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), MenuConst.PATH_FORMAT_INVALID);
        }
    }

    /**
     * 校验name格式（PascalCase）
     */
    private void validateNameFormat(String name) {
        if (name.length() > MenuConst.NAME_MAX_LENGTH) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), MenuConst.NAME_FORMAT_INVALID);
        }
        if (!ValidateUtils.isValidName(name)) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), MenuConst.NAME_FORMAT_INVALID);
        }
    }

    /**
     * 校验component格式
     */
    private void validateComponentFormat(String component) {
        if (component.length() > MenuConst.COMPONENT_MAX_LENGTH) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), MenuConst.COMPONENT_FORMAT_INVALID);
        }
        if (!ValidateUtils.isValidComponent(component)) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), MenuConst.COMPONENT_FORMAT_INVALID);
        }
    }

    /**
     * 校验authMark格式
     */
    private void validateAuthMarkFormat(String authMark) {
        if (authMark.length() > MenuConst.AUTH_MARK_MAX_LENGTH) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), MenuConst.AUTH_MARK_LENGTH_INVALID);
        }
        if (!ValidateUtils.isValidAuthMark(authMark)) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), MenuConst.AUTH_MARK_FORMAT_INVALID);
        }
    }

    /**
     * 校验图标格式
     */
    private void validateIconFormat(String icon) {
        if (StringUtils.isBlank(icon)) {
            return;
        }
        if (icon.length() > MenuConst.ICON_MAX_LENGTH) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), MenuConst.ICON_FORMAT_INVALID);
        }
        if (!ValidateUtils.isValidIcon(icon)) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), MenuConst.ICON_FORMAT_INVALID);
        }
    }

    /**
     * 校验activePath格式
     */
    private void validateActivePathFormat(String activePath) {
        if (activePath.length() > MenuConst.ACTIVE_PATH_MAX_LENGTH) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), MenuConst.ACTIVE_PATH_FORMAT_INVALID);
        }
        if (!activePath.startsWith("/")) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), MenuConst.ACTIVE_PATH_FORMAT_INVALID);
        }
    }

}
