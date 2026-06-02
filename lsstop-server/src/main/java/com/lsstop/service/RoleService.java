package com.lsstop.service;

import com.lsstop.domain.dto.AddRoleDTO;
import com.lsstop.domain.dto.UpdateRoleDTO;
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
}
