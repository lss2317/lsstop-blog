package com.lsstop.mapper;

import com.lsstop.domain.vo.ApiPermissionNodeVO;

import java.util.List;

/**
 * 接口权限数据访问层
 *
 * @author lishusheng
 * @date 2026/06/04
 */
public interface ApiPermissionMapper {

    /**
     * 查询系统所有启用的接口权限（精简字段，用于构建权限树）
     *
     * @return 全量启用接口权限列表（已按 sort, id 排序）
     */
    List<ApiPermissionNodeVO> selectAllApiPermissions();

}
