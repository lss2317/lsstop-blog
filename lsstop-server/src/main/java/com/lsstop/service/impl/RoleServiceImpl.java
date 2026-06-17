package com.lsstop.service.impl;

import com.lsstop.constant.RedisConst;
import com.lsstop.constant.RoleConst;
import com.lsstop.domain.dto.AddRoleDTO;
import com.lsstop.domain.dto.UpdateRoleApiPermissionDTO;
import com.lsstop.domain.dto.UpdateRoleDTO;
import com.lsstop.domain.dto.UpdateRoleMenuDTO;
import com.lsstop.domain.vo.RoleOptionVO;
import com.lsstop.domain.vo.RoleVO;
import com.lsstop.enums.StatusEnum;
import com.lsstop.exception.BusinessException;
import com.lsstop.mapper.RoleMapper;
import com.lsstop.service.RoleService;
import com.lsstop.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 角色服务实现类
 *
 * @author lishusheng
 * @date 2026/06/02
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private RedisUtils redisUtils;

    /**
     * 分页查询角色列表
     *
     * @param current   当前页码
     * @param size      每页数量
     * @param roleName  角色名称（模糊搜索）
     * @param roleCode  角色编码
     * @param isEnabled 是否启用
     * @return 角色列表
     */
    @Override
    public List<RoleVO> listRoles(Integer current, Integer size, String roleName, String roleCode, Integer isEnabled) {
        int offset = (current - 1) * size;
        return roleMapper.selectRoleList(offset, size, roleName, roleCode, isEnabled);
    }

    /**
     * 统计角色总数
     *
     * @param roleName  角色名称（模糊搜索）
     * @param roleCode  角色编码
     * @param isEnabled 是否启用
     * @return 角色总数
     */
    @Override
    public Integer countRoleTotal(String roleName, String roleCode, Integer isEnabled) {
        return roleMapper.countRoleTotal(roleName, roleCode, isEnabled);
    }

    /**
     * 新增角色
     *
     * @param dto 新增角色参数
     */
    @Override
    public void addRole(AddRoleDTO dto) {
        // 校验角色编码是否已存在
        RoleVO existing = roleMapper.selectByRoleCode(dto.getRoleCode(), null);
        if (existing != null) {
            throw new BusinessException(StatusEnum.USERNAME_OR_EMAIL_EXIST, RoleConst.ROLE_CODE_EXISTS);
        }
        roleMapper.insertRole(dto);
        // 清除角色选项缓存
        redisUtils.delete(RedisConst.ROLE_OPTIONS);
    }

    /**
     * 编辑角色
     *
     * @param dto 编辑角色参数
     */
    @Override
    public void updateRole(UpdateRoleDTO dto) {
        // 校验角色是否存在
        RoleVO role = roleMapper.selectRoleById(dto.getId());
        if (role == null) {
            throw new BusinessException(StatusEnum.NOT_FOUND, RoleConst.ROLE_NOT_FOUND);
        }
        // 校验角色编码是否与其他角色重复
        RoleVO existing = roleMapper.selectByRoleCode(dto.getRoleCode(), dto.getId());
        if (existing != null) {
            throw new BusinessException(StatusEnum.USERNAME_OR_EMAIL_EXIST, RoleConst.ROLE_CODE_EXISTS);
        }
        roleMapper.updateRole(dto);
        // 清除角色选项缓存
        redisUtils.delete(RedisConst.ROLE_OPTIONS);
        // 清除拥有该角色的用户的菜单缓存（启用/禁用状态变更时需刷新）
        clearRoleUserCache(dto.getId());
    }

    /**
     * 删除角色
     *
     * @param id 角色ID
     */
    @Override
    public void deleteRole(Integer id) {
        // 校验角色是否存在
        RoleVO role = roleMapper.selectRoleById(id);
        if (role == null) {
            throw new BusinessException(StatusEnum.NOT_FOUND, RoleConst.ROLE_NOT_FOUND);
        }
        // 校验是否有用户分配了该角色
        Integer userCount = roleMapper.countUsersByRoleId(id);
        if (userCount != null && userCount > 0) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), RoleConst.ROLE_HAS_USERS);
        }
        roleMapper.deleteById(id, System.currentTimeMillis());
        // 清除角色选项缓存
        redisUtils.delete(RedisConst.ROLE_OPTIONS);
        // 清除拥有该角色的用户的菜单缓存
        clearRoleUserCache(id);
    }

    /**
     * 获取角色已关联的菜单ID列表（用于权限配置弹窗回显）
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    @Override
    public List<Integer> getRoleMenuIds(Integer roleId) {
        // 校验角色是否存在
        RoleVO role = roleMapper.selectRoleById(roleId);
        if (role == null) {
            throw new BusinessException(StatusEnum.NOT_FOUND, RoleConst.ROLE_NOT_FOUND);
        }
        return roleMapper.selectMenuIdsByRoleId(roleId);
    }

    /**
     * 修改角色菜单权限（差量更新）
     * <p>接收全量菜单ID列表，后端与现有权限做差集计算，
     * 多的新增，少的软删除
     *
     * @param dto 修改角色菜单权限参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoleMenuPermission(UpdateRoleMenuDTO dto) {
        Integer roleId = dto.getRoleId();
        // 校验角色是否存在
        RoleVO role = roleMapper.selectRoleById(roleId);
        if (role == null) {
            throw new BusinessException(StatusEnum.NOT_FOUND, RoleConst.ROLE_NOT_FOUND);
        }

        Set<Integer> newMenuIds = new HashSet<>(dto.getMenuIds());
        // 查询当前激活的菜单ID
        Set<Integer> activeExisting = new HashSet<>(roleMapper.selectMenuIdsByRoleId(roleId));

        // 计算需要新增的（不在当前激活列表中）
        List<Integer> toInsert = new ArrayList<>();
        for (Integer menuId : newMenuIds) {
            if (!activeExisting.contains(menuId)) {
                toInsert.add(menuId);
            }
        }

        // 计算需要软删除的（当前激活但不在新列表中）
        List<Integer> toSoftDelete = new ArrayList<>();
        for (Integer menuId : activeExisting) {
            if (!newMenuIds.contains(menuId)) {
                toSoftDelete.add(menuId);
            }
        }

        // 执行差量操作
        if (!toInsert.isEmpty()) {
            roleMapper.batchInsertRoleMenu(roleId, toInsert);
        }
        if (!toSoftDelete.isEmpty()) {
            roleMapper.batchSoftDeleteRoleMenu(roleId, toSoftDelete, System.currentTimeMillis());
        }

        // 清除拥有该角色的用户的菜单缓存
        clearRoleUserCache(roleId);
    }

    /**
     * 获取角色已关联的接口权限ID列表
     *
     * @param roleId 角色ID
     * @return 接口权限ID列表
     */
    @Override
    public List<Integer> getRoleApiPermissionIds(Integer roleId) {
        // 校验角色是否存在
        RoleVO role = roleMapper.selectRoleById(roleId);
        if (role == null) {
            throw new BusinessException(StatusEnum.NOT_FOUND, RoleConst.ROLE_NOT_FOUND);
        }
        return roleMapper.selectApiPermissionIdsByRoleId(roleId);
    }

    /**
     * 修改角色接口权限（差量更新）
     * <p>接收全量接口权限ID列表，后端与现有权限做差集计算，
     * 多的新增，少的软删除
     *
     * @param dto 修改角色接口权限参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoleApiPermission(UpdateRoleApiPermissionDTO dto) {
        Integer roleId = dto.getRoleId();
        // 校验角色是否存在
        RoleVO role = roleMapper.selectRoleById(roleId);
        if (role == null) {
            throw new BusinessException(StatusEnum.NOT_FOUND, RoleConst.ROLE_NOT_FOUND);
        }

        Set<Integer> newApiPermissionIds = new HashSet<>(dto.getApiIds());
        // 查询当前激活的接口权限ID
        Set<Integer> activeExisting = new HashSet<>(roleMapper.selectApiPermissionIdsByRoleId(roleId));

        // 计算需要新增的（不在当前激活列表中）
        List<Integer> toInsert = new ArrayList<>();
        for (Integer apiPermissionId : newApiPermissionIds) {
            if (!activeExisting.contains(apiPermissionId)) {
                toInsert.add(apiPermissionId);
            }
        }

        // 计算需要软删除的（当前激活但不在新列表中）
        List<Integer> toSoftDelete = new ArrayList<>();
        for (Integer apiPermissionId : activeExisting) {
            if (!newApiPermissionIds.contains(apiPermissionId)) {
                toSoftDelete.add(apiPermissionId);
            }
        }

        // 执行差量操作
        if (!toInsert.isEmpty()) {
            roleMapper.batchInsertRoleApiPermission(roleId, toInsert);
        }
        if (!toSoftDelete.isEmpty()) {
            roleMapper.batchSoftDeleteRoleApiPermission(roleId, toSoftDelete, System.currentTimeMillis());
        }

        // 清除拥有该角色的用户的缓存
        clearRoleUserCache(roleId);
    }

    /**
     * 获取所有启用角色（仅返回ID和名称，用于下拉选项）
     * <p>优先从缓存获取，缓存未命中时查询数据库并写入缓存
     *
     * @return 角色选项列表
     */
    @Override
    public List<RoleOptionVO> listAllRoleOptions() {
        String cacheKey = RedisConst.ROLE_OPTIONS;
        List<RoleOptionVO> cached = redisUtils.getList(cacheKey, RoleOptionVO.class);
        if (cached != null) {
            return cached;
        }
        List<RoleOptionVO> list = roleMapper.selectAllRoleOptions();
        redisUtils.set(cacheKey, list, RedisConst.EXPIRE_ONE_DAY);
        return list;
    }

    /**
     * 清除拥有指定角色的用户的菜单缓存
     *
     * @param roleId 角色ID
     */
    private void clearRoleUserCache(Integer roleId) {
        List<String> userIds = roleMapper.selectUserIdsByRoleId(roleId);
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        List<String> cacheKeys = new ArrayList<>();
        for (String userId : userIds) {
            cacheKeys.add(RedisConst.USER_MENU_TREE + userId);
            cacheKeys.add(RedisConst.USER_EFFECTIVE_API_PERMISSIONS + userId);
            cacheKeys.add(RedisConst.USER_MENU_IDS + userId);
            cacheKeys.add(RedisConst.USER_API_PERMISSION_IDS + userId);
        }
        redisUtils.delete(cacheKeys);
    }
}
