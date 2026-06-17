package com.lsstop.mapper;

import com.lsstop.domain.dto.AddRoleDTO;
import com.lsstop.domain.dto.UpdateRoleDTO;
import com.lsstop.domain.vo.RoleOptionVO;
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

    /**
     * 软删除角色
     *
     * @param id        角色ID
     * @param deletedAt 删除时间戳
     */
    void deleteById(@Param("id") Integer id, @Param("deletedAt") Long deletedAt);

    /**
     * 统计已分配该角色的用户数
     *
     * @param id 角色ID
     * @return 已分配该角色的用户数
     */
    Integer countUsersByRoleId(@Param("id") Integer id);

    /**
     * 查询角色已关联的菜单ID列表（用于权限配置弹窗回显）
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Integer> selectMenuIdsByRoleId(@Param("roleId") Integer roleId);

    /**
     * 查询拥有指定角色的用户ID列表
     *
     * @param roleId 角色ID
     * @return 用户ID列表
     */
    List<String> selectUserIdsByRoleId(@Param("roleId") Integer roleId);

    /**
     * 批量新增角色菜单关联（已存在活跃记录时幂等更新）
     *
     * @param roleId  角色ID
     * @param menuIds 菜单ID列表
     */
    void batchInsertRoleMenu(@Param("roleId") Integer roleId, @Param("menuIds") List<Integer> menuIds);

    /**
     * 批量软删除角色菜单关联
     *
     * @param roleId    角色ID
     * @param menuIds   菜单ID列表
     * @param deletedAt 删除时间戳
     */
    void batchSoftDeleteRoleMenu(@Param("roleId") Integer roleId,
                                  @Param("menuIds") List<Integer> menuIds,
                                  @Param("deletedAt") Long deletedAt);

    /**
     * 查询角色已关联的接口权限ID列表
     *
     * @param roleId 角色ID
     * @return 接口权限ID列表
     */
    List<Integer> selectApiPermissionIdsByRoleId(@Param("roleId") Integer roleId);

    /**
     * 批量新增角色接口权限关联（已存在活跃记录时幂等更新）
     *
     * @param roleId           角色ID
     * @param apiPermissionIds 接口权限ID列表
     */
    void batchInsertRoleApiPermission(@Param("roleId") Integer roleId,
                                       @Param("apiPermissionIds") List<Integer> apiPermissionIds);

    /**
     * 批量软删除角色接口权限关联
     *
     * @param roleId           角色ID
     * @param apiPermissionIds 接口权限ID列表
     * @param deletedAt        删除时间戳
     */
    void batchSoftDeleteRoleApiPermission(@Param("roleId") Integer roleId,
                                           @Param("apiPermissionIds") List<Integer> apiPermissionIds,
                                           @Param("deletedAt") Long deletedAt);

    /**
     * 查询所有启用角色（仅返回ID和名称，用于下拉选项）
     *
     * @return 角色选项列表
     */
    List<RoleOptionVO> selectAllRoleOptions();
}
