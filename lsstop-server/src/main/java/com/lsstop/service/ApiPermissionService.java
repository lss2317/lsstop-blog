package com.lsstop.service;

import com.lsstop.domain.vo.ApiPermissionAdminVO;
import com.lsstop.domain.vo.ApiPermissionNodeVO;

import java.util.List;

/**
 * 接口权限服务接口
 *
 * @author lishusheng
 * @date 2026/06/04
 */
public interface ApiPermissionService {

    /**
     * 获取全量接口权限树
     * <p>返回系统所有启用接口的树形结构，包含目录和接口两类节点
     *
     * @return 接口权限树
     */
    List<ApiPermissionNodeVO> getApiPermissionTree();

    /**
     * 获取接口权限列表（后台管理用，树形结构）
     * <p>支持关键词、请求方法、启用状态过滤
     *
     * @param keyword       关键词（模糊搜索：描述、接口路径）
     * @param requestMethod 请求方法
     * @param isEnabled     是否启用
     * @return 接口权限树
     */
    List<ApiPermissionAdminVO> listApiPermissions(String keyword, String requestMethod,
                                                   Integer isEnabled);

}
