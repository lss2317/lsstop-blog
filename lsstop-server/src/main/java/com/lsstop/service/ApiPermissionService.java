package com.lsstop.service;

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

}
