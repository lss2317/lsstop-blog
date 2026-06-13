package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.annotation.OperationLog;
import com.lsstop.common.Result;
import com.lsstop.domain.dto.AddApiPermissionDTO;
import com.lsstop.domain.dto.ApiPermissionQueryDTO;
import com.lsstop.domain.dto.UpdateApiPermissionDTO;
import com.lsstop.domain.vo.ApiPermissionAdminVO;
import com.lsstop.enums.OperationModuleEnum;
import com.lsstop.enums.OperationTypeEnum;
import com.lsstop.service.ApiPermissionService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 接口权限控制层
 *
 * @author lishusheng
 * @date 2026/06/12
 */
@RestController
public class ApiPermissionController {

    @Resource
    private ApiPermissionService apiPermissionService;

    /**
     * 获取接口权限列表（后台管理用，树形结构）
     * <p>支持关键词、请求方法、启用状态过滤
     *
     * @param query 查询参数
     * @return 接口权限树
     */
    @GetMapping("/admin/api-permission/list")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<List<ApiPermissionAdminVO>> listApiPermissions(ApiPermissionQueryDTO query) {
        return Result.success(apiPermissionService.listApiPermissions(
                query.getKeyword(), query.getRequestMethod(), query.getIsEnabled()));
    }

    /**
     * 新增接口权限
     *
     * @param dto 新增参数
     * @return 操作结果
     */
    @PostMapping("/admin/api-permission/add")
    @AccessLimit(seconds = 60, maxCount = 30)
    @OperationLog(module = OperationModuleEnum.API_PERMISSION, type = OperationTypeEnum.ADD, description = "新增接口权限")
    public Result<Void> addApiPermission(@RequestBody @Validated AddApiPermissionDTO dto) {
        apiPermissionService.addApiPermission(dto);
        return Result.success();
    }

    /**
     * 编辑接口权限
     * <p>目录↔接口类型不允许互转
     *
     * @param dto 编辑参数
     * @return 操作结果
     */
    @PutMapping("/admin/api-permission/update")
    @AccessLimit(seconds = 60, maxCount = 30)
    @OperationLog(module = OperationModuleEnum.API_PERMISSION, type = OperationTypeEnum.UPDATE, description = "编辑接口权限")
    public Result<Void> updateApiPermission(@RequestBody @Validated UpdateApiPermissionDTO dto) {
        apiPermissionService.updateApiPermission(dto);
        return Result.success();
    }

    /**
     * 删除接口权限（软删除）
     * <p>存在子权限时不允许删除，需先删除子权限
     *
     * @param id 权限ID
     * @return 操作结果
     */
    @DeleteMapping("/admin/api-permission/delete/{id}")
    @AccessLimit(seconds = 60, maxCount = 30)
    @OperationLog(module = OperationModuleEnum.API_PERMISSION, type = OperationTypeEnum.DELETE, description = "删除接口权限")
    public Result<Void> deleteApiPermission(@PathVariable Integer id) {
        apiPermissionService.deleteApiPermission(id);
        return Result.success();
    }

}
