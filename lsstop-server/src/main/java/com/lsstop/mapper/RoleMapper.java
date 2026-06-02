package com.lsstop.mapper;

import com.lsstop.domain.dto.AddRoleDTO;
import com.lsstop.domain.dto.UpdateRoleDTO;
import com.lsstop.domain.vo.RoleVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色数据访问层
 *
 * @author lishusheng
 * @date 2026/06/02
 */
public interface RoleMapper {

    /**
     * 分页查询角色列表
     *
     * @param offset    偏移量
     * @param size      每页数量
     * @param roleName  角色名称（模糊搜索）
     * @param roleCode  角色编码
     * @param isEnabled 是否启用
     * @return 角色列表
     */
    List<RoleVO> selectRoleList(@Param("offset") Integer offset,
                                @Param("size") Integer size,
                                @Param("roleName") String roleName,
                                @Param("roleCode") String roleCode,
                                @Param("isEnabled") Integer isEnabled);

    /**
     * 统计角色总数
     *
     * @param roleName  角色名称（模糊搜索）
     * @param roleCode  角色编码
     * @param isEnabled 是否启用
     * @return 角色总数
     */
    Integer countRoleTotal(@Param("roleName") String roleName,
                           @Param("roleCode") String roleCode,
                           @Param("isEnabled") Integer isEnabled);

    /**
     * 新增角色
     *
     * @param dto 新增角色参数
     */
    void insertRole(AddRoleDTO dto);

    /**
     * 编辑角色
     *
     * @param dto 编辑角色参数
     */
    void updateRole(UpdateRoleDTO dto);

    /**
     * 根据ID查询角色
     *
     * @param id 角色ID
     * @return 角色信息
     */
    RoleVO selectRoleById(@Param("id") Integer id);

    /**
     * 根据角色编码查询角色（排除指定ID）
     *
     * @param roleCode 角色编码
     * @param excludeId 排除的角色ID
     * @return 角色信息
     */
    RoleVO selectByRoleCode(@Param("roleCode") String roleCode, @Param("excludeId") Integer excludeId);
}
