package com.lsstop.service;

import com.lsstop.domain.dto.AddRoleDTO;
import com.lsstop.domain.dto.UpdateRoleApiPermissionDTO;
import com.lsstop.domain.dto.UpdateRoleDTO;
import com.lsstop.domain.dto.UpdateRoleMenuDTO;
import com.lsstop.domain.vo.RoleVO;

import java.util.List;

/**
 * 角色服务
 *
 * @author lishusheng
 * @date 2026/06/02
 */
public interface RoleService {

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
    List<RoleVO> listRoles(Integer current, Integer size, String roleName, String roleCode, Integer isEnabled);

    /**
     * 统计角色总数
     *
     * @param roleName  角色名称（模糊搜索）
     * @param roleCode  角色编码
     * @param isEnabled 是否启用
     * @return 角色总数
     */
    Integer countRoleTotal(String roleName, String roleCode, Integer isEnabled);

    /**
     * 新增角色
     *
     * @param dto 新增角色参数
     */
    void addRole(AddRoleDTO dto);

    /**
     * 编辑角色
     *
     * @param dto 编辑角色参数
     */
    void updateRole(UpdateRoleDTO dto);

    /**
     * 删除角色
     *
     * @param id 角色ID
     */
    void deleteRole(Integer id);

    /**
     * 获取角色已关联的菜单ID列表（用于权限配置弹窗回显）
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Integer> getRoleMenuIds(Integer roleId);

    /**
     * 修改角色菜单权限（差量更新）
     * <p>接收全量菜单ID列表，后端与现有权限做差集计算，
     * 多的新增，少的软删除
     *
     * @param dto 修改角色菜单权限参数
     */
    void updateRoleMenuPermission(UpdateRoleMenuDTO dto);

    /**
     * 获取角色已关联的接口权限ID列表
     *
     * @param roleId 角色ID
     * @return 接口权限ID列表
     */
    List<Integer> getRoleApiPermissionIds(Integer roleId);

    /**
     * 修改角色接口权限（差量更新）
     * <p>接收全量接口权限ID列表，后端与现有权限做差集计算，
     * 多的新增，少的软删除
     *
     * @param dto 修改角色接口权限参数
     */
    void updateRoleApiPermission(UpdateRoleApiPermissionDTO dto);
}
