package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.annotation.OperationLog;
import com.lsstop.common.Result;
import com.lsstop.constant.CommentConst;
import com.lsstop.domain.dto.AddRoleDTO;
import com.lsstop.domain.dto.UpdateRoleDTO;
import com.lsstop.domain.vo.RolePageVO;
import com.lsstop.enums.OperationModuleEnum;
import com.lsstop.enums.OperationTypeEnum;
import com.lsstop.enums.StatusEnum;
import com.lsstop.exception.BusinessException;
import com.lsstop.service.RoleService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @PostMapping("/admin/role/update")
    @AccessLimit(seconds = 60, maxCount = 30)
    @OperationLog(module = OperationModuleEnum.ROLE, type = OperationTypeEnum.UPDATE, description = "编辑角色")
    public Result<Void> updateRole(@RequestBody @Validated UpdateRoleDTO dto) {
        roleService.updateRole(dto);
        return Result.success();
    }
}
