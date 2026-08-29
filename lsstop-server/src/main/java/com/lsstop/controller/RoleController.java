package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.annotation.OperationLog;
import com.lsstop.common.Result;
import com.lsstop.constant.CommentConst;
import com.lsstop.domain.dto.AddRoleDTO;
import com.lsstop.domain.dto.UpdateRoleApiPermissionDTO;
import com.lsstop.domain.dto.UpdateRoleDTO;
import com.lsstop.domain.dto.UpdateRoleMenuDTO;
import com.lsstop.domain.vo.ApiPermissionNodeVO;
import com.lsstop.domain.vo.MenuPermissionVO;
import com.lsstop.domain.vo.RoleOptionVO;
import com.lsstop.domain.vo.RolePageVO;
import com.lsstop.enums.OperationModuleEnum;
import com.lsstop.enums.OperationTypeEnum;
import com.lsstop.enums.StatusEnum;
import com.lsstop.exception.BusinessException;
import com.lsstop.service.ApiPermissionService;
import com.lsstop.service.MenuService;
import com.lsstop.service.RoleService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 角色控制层
 *
 * @author lishusheng
 * @date 2026/06/02
 */
@RestController
public class RoleController {

    @Resource
    private RoleService roleService;

    @Resource
    private MenuService menuService;

    @Resource
    private ApiPermissionService apiPermissionService;

    /**
     * 获取角色列表（分页）
     *
     * @param current   当前页码
     * @param size      每页条数
     * @param roleName  角色名称（模糊搜索）
     * @param roleCode  角色编码
     * @param isEnabled 是否启用
     * @return 角色列表及总数
     */
    @GetMapping("/admin/role/list")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<RolePageVO> listRole(@RequestParam Integer current,
                                       @RequestParam Integer size,
                                       @RequestParam(required = false) String roleName,
                                       @RequestParam(required = false) String roleCode,
                                       @RequestParam(required = false) Integer isEnabled) {
        if (current < 1) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), CommentConst.INVALID_PAGE_PARAM);
        }
        if (size < 1) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), CommentConst.INVALID_PAGE_PARAM);
        }
        RolePageVO pageVO = new RolePageVO(
                roleService.listRoles(current, size, roleName, roleCode, isEnabled),
                current, size, roleService.countRoleTotal(roleName, roleCode, isEnabled)
        );
        return Result.success(pageVO);
    }

    /**
     * 新增角色
     *
     * @param dto 新增角色参数
     * @return 操作结果
     */
    @PostMapping("/admin/role/add")
    @AccessLimit(seconds = 60, maxCount = 30)
    @OperationLog(module = OperationModuleEnum.ROLE, type = OperationTypeEnum.ADD, description = "新增角色")
    public Result<Void> addRole(@RequestBody @Validated AddRoleDTO dto) {
        roleService.addRole(dto);
        return Result.success();
    }

    /**
     * 编辑角色
     *
     * @param dto 编辑角色参数
     * @return 操作结果
     */
    @PutMapping("/admin/role/update")
    @AccessLimit(seconds = 60, maxCount = 30)
    @OperationLog(module = OperationModuleEnum.ROLE, type = OperationTypeEnum.UPDATE, description = "编辑角色")
    public Result<Void> updateRole(@RequestBody @Validated UpdateRoleDTO dto) {
        roleService.updateRole(dto);
        return Result.success();
    }

    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return 操作结果
     */
    @DeleteMapping("/admin/role/delete/{id}")
    @AccessLimit(seconds = 60, maxCount = 30)
    @OperationLog(module = OperationModuleEnum.ROLE, type = OperationTypeEnum.DELETE, description = "删除角色")
    public Result<Void> deleteRole(@PathVariable Integer id) {
        roleService.deleteRole(id);
        return Result.success();
    }

    /**
     * 获取角色已关联的菜单ID列表（用于权限配置弹窗回显）
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    @GetMapping("/admin/role/menu-permission")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<List<Integer>> getRoleMenuIds(@RequestParam Integer roleId) {
        return Result.success(roleService.getRoleMenuIds(roleId));
    }

    /**
     * 获取全量菜单权限树（用于权限配置弹窗）
     * <p>返回系统所有启用菜单的精简树形结构，包含目录、菜单、按钮三类节点
     *
     * @return 菜单权限树
     */
    @GetMapping("/admin/role/menu-permission/tree")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<List<MenuPermissionVO>> getMenuPermissionTree() {
        return Result.success(menuService.getMenuPermissionTree());
    }

    /**
     * 修改角色菜单权限
     * <p>接收全量菜单ID列表，后端与现有权限做差集计算后更新
     *
     * @param dto 修改角色菜单权限参数
     * @return 操作结果
     */
    @PutMapping("/admin/role/menu-permission")
    @AccessLimit(seconds = 60, maxCount = 30)
    @OperationLog(module = OperationModuleEnum.ROLE, type = OperationTypeEnum.AUTHORIZATION, description = "修改角色菜单权限")
    public Result<Void> updateRoleMenuPermission(@RequestBody @Validated UpdateRoleMenuDTO dto) {
        roleService.updateRoleMenuPermission(dto);
        return Result.success();
    }

    /**
     * 获取全量接口权限树
     * <p>返回系统所有启用接口的树形结构，用于接口权限配置界面展示
     *
     * @return 接口权限树
     */
    @GetMapping("/admin/role/api-permission/tree")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<List<ApiPermissionNodeVO>> getApiPermissionTree() {
        return Result.success(apiPermissionService.getApiPermissionTree());
    }

    /**
     * 获取角色已关联的接口权限ID列表（用于权限配置弹窗回显）
     *
     * @param roleId 角色ID
     * @return 接口权限ID列表
     */
    @GetMapping("/admin/role/api-permission")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<List<Integer>> getRoleApiPermissionIds(@RequestParam Integer roleId) {
        return Result.success(roleService.getRoleApiPermissionIds(roleId));
    }

    /**
     * 修改角色接口权限
     * <p>接收全量接口权限ID列表，后端与现有权限做差集计算后更新
     *
     * @param dto 修改角色接口权限参数
     * @return 操作结果
     */
    @PutMapping("/admin/role/api-permission")
    @AccessLimit(seconds = 60, maxCount = 30)
    @OperationLog(module = OperationModuleEnum.ROLE, type = OperationTypeEnum.AUTHORIZATION, description = "修改角色接口权限")
    public Result<Void> updateRoleApiPermission(@RequestBody @Validated UpdateRoleApiPermissionDTO dto) {
        roleService.updateRoleApiPermission(dto);
        return Result.success();
    }

    /**
     * 获取所有启用角色（仅返回ID和名称，用于下拉选项）
     *
     * @return 角色选项列表
     */
    @GetMapping("/admin/role/options")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<List<RoleOptionVO>> listAllRoleOptions() {
        return Result.success(roleService.listAllRoleOptions());
    }
}
