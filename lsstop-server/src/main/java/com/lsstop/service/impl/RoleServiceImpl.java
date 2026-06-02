package com.lsstop.service.impl;

import com.lsstop.constant.RoleConst;
import com.lsstop.domain.dto.AddRoleDTO;
import com.lsstop.domain.dto.UpdateRoleDTO;
import com.lsstop.domain.vo.RoleVO;
import com.lsstop.enums.StatusEnum;
import com.lsstop.exception.BusinessException;
import com.lsstop.mapper.RoleMapper;
import com.lsstop.service.RoleService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

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
    }
}
