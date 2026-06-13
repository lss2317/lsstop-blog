package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.common.Result;
import com.lsstop.domain.dto.ApiPermissionQueryDTO;
import com.lsstop.domain.vo.ApiPermissionAdminVO;
import com.lsstop.service.ApiPermissionService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
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

}
