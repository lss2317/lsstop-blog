package com.lsstop.service;

import com.lsstop.domain.dto.AddApiPermissionDTO;
import com.lsstop.domain.dto.UpdateApiPermissionDTO;
import com.lsstop.domain.vo.ApiPermissionAdminVO;
import com.lsstop.domain.vo.ApiPermissionNodeVO;

import java.util.List;
import java.util.Set;

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

    /**
     * 新增接口权限
     *
     * @param dto 新增参数
     */
    void addApiPermission(AddApiPermissionDTO dto);

    /**
     * 编辑接口权限
     * <p>目录↔接口类型不允许互转
     *
     * @param dto 编辑参数
     */
    void updateApiPermission(UpdateApiPermissionDTO dto);

    /**
     * 删除接口权限（软删除）
     * <p>存在子权限时不允许删除，需先删除子权限
     *
     * @param id 权限ID
     */
    void deleteApiPermission(Integer id);

    /**
     * 获取用户有效接口权限集合
     * <p>基于 blog_api_permission 三表体系计算：角色授予 ∪ 用户额外授予 - 用户额外排除
     * <p>返回格式如：GET:/admin/article/list、DELETE:/admin/article/*
     *
     * @param userId 用户uid
     * @return 用户有效接口权限模式集合
     */
    Set<String> getUserEffectiveApiPermissions(String userId);

    /**
     * 获取系统全量已注册接口权限模式
     * <p>用于判断当前请求是否受权限控制；未注册的 URL 默认放行
     * <p>返回格式如：GET:/admin/article/list、DELETE:/admin/article/*
     *
     * @return 全局已注册接口权限模式集合
     */
    Set<String> getAllRegisteredApiPermissions();

}
