package com.lsstop.service.impl;

import com.lsstop.constant.RedisConst;
import com.lsstop.domain.vo.ApiPermissionAdminVO;
import com.lsstop.domain.vo.ApiPermissionNodeVO;
import com.lsstop.mapper.ApiPermissionMapper;
import com.lsstop.service.ApiPermissionService;
import com.lsstop.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 接口权限服务实现类
 *
 * @author lishusheng
 * @date 2026/06/04
 */
@Service
public class ApiPermissionServiceImpl implements ApiPermissionService {

    @Resource
    private ApiPermissionMapper apiPermissionMapper;

    @Resource
    private RedisUtils redisUtils;

    @Override
    public List<ApiPermissionNodeVO> getApiPermissionTree() {
        String cacheKey = RedisConst.API_PERMISSION_TREE;
        // 先从缓存获取
        List<ApiPermissionNodeVO> cachedTree = redisUtils.getList(cacheKey, ApiPermissionNodeVO.class);
        if (cachedTree != null) {
            return cachedTree;
        }
        // 缓存不存在，查询数据库
        List<ApiPermissionNodeVO> flatList = apiPermissionMapper.selectAllApiPermissions();
        List<ApiPermissionNodeVO> tree = buildTree(flatList);
        // 写入缓存，过期时间1天
        redisUtils.set(cacheKey, tree, RedisConst.EXPIRE_ONE_DAY);
        return tree;
    }

    @Override
    public List<ApiPermissionAdminVO> listApiPermissions(String keyword, String requestMethod,
                                                          Integer isEnabled) {
        List<ApiPermissionAdminVO> flatList = apiPermissionMapper.selectApiPermissions(keyword, requestMethod, isEnabled);
        // 补全缺失的父级，确保搜索命中的子节点不被丢弃
        flatList = fillMissingAdminParents(flatList);
        // 补全父级后重新排序，确保补全的目录在正确的排序位置
        flatList.sort(Comparator.comparing(ApiPermissionAdminVO::getSort).thenComparing(ApiPermissionAdminVO::getId));
        return buildAdminTree(flatList);
    }

    /**
     * 将扁平管理列表构建为树形结构
     *
     * @param flatList 扁平管理列表（已按 sort, id 排序）
     * @return 树形管理列表
     */
    private List<ApiPermissionAdminVO> buildAdminTree(List<ApiPermissionAdminVO> flatList) {
        Map<Integer, ApiPermissionAdminVO> nodeMap = new LinkedHashMap<>();
        for (ApiPermissionAdminVO node : flatList) {
            node.setChildren(new ArrayList<>());
            nodeMap.put(node.getId(), node);
        }

        List<ApiPermissionAdminVO> rootNodes = new ArrayList<>();
        for (ApiPermissionAdminVO node : flatList) {
            if (node.getParentId() == null || node.getParentId() == 0) {
                rootNodes.add(node);
            } else {
                ApiPermissionAdminVO parent = nodeMap.get(node.getParentId());
                if (parent != null) {
                    parent.getChildren().add(node);
                }
            }
        }

        removeAdminEmptyChildren(rootNodes);
        return rootNodes;
    }

    /**
     * 补全管理后台接口权限列表中缺失的父级
     * <p>当子接口在搜索命中但父级不在时，向上递归补全所有祖先，
     * 确保子接口在构建树结构时不会被丢弃
     *
     * @param flatList 原始扁平接口权限列表
     * @return 补全后的扁平接口权限列表
     */
    private List<ApiPermissionAdminVO> fillMissingAdminParents(List<ApiPermissionAdminVO> flatList) {
        if (flatList.isEmpty()) {
            return flatList;
        }
        List<ApiPermissionAdminVO> result = new ArrayList<>(flatList);
        Set<Integer> existingIds = result.stream()
                .map(ApiPermissionAdminVO::getId)
                .collect(Collectors.toSet());

        // 收集所有缺失的父级ID
        Set<Integer> missingIds = result.stream()
                .map(ApiPermissionAdminVO::getParentId)
                .filter(p -> p != null && p != 0)
                .filter(p -> !existingIds.contains(p))
                .collect(Collectors.toSet());

        // 向上递归补全所有祖先
        while (!missingIds.isEmpty()) {
            List<ApiPermissionAdminVO> parents = apiPermissionMapper.selectApiPermissionsByIds(new ArrayList<>(missingIds));
            if (parents.isEmpty()) {
                break;
            }
            result.addAll(parents);
            for (ApiPermissionAdminVO parent : parents) {
                existingIds.add(parent.getId());
            }
            missingIds.clear();
            for (ApiPermissionAdminVO parent : parents) {
                if (parent.getParentId() != null && parent.getParentId() != 0
                        && !existingIds.contains(parent.getParentId())) {
                    missingIds.add(parent.getParentId());
                }
            }
        }
        return result;
    }

    /**
     * 递归清理空的 children，设为 null
     */
    private void removeAdminEmptyChildren(List<ApiPermissionAdminVO> nodes) {
        for (ApiPermissionAdminVO node : nodes) {
            if (node.getChildren() != null && node.getChildren().isEmpty()) {
                node.setChildren(null);
            } else if (node.getChildren() != null) {
                removeAdminEmptyChildren(node.getChildren());
            }
        }
    }

    /**
     * 将扁平权限列表构建为树形结构
     *
     * @param flatList 扁平权限列表（已按 sort, id 排序）
     * @return 树形权限列表
     */
    private List<ApiPermissionNodeVO> buildTree(List<ApiPermissionNodeVO> flatList) {
        // 使用 LinkedHashMap 保持插入顺序（即排序顺序）
        Map<Integer, ApiPermissionNodeVO> nodeMap = new LinkedHashMap<>();
        for (ApiPermissionNodeVO node : flatList) {
            node.setChildren(new ArrayList<>());
            nodeMap.put(node.getId(), node);
        }

        List<ApiPermissionNodeVO> rootNodes = new ArrayList<>();
        for (ApiPermissionNodeVO node : flatList) {
            if (node.getParentId() == null || node.getParentId() == 0) {
                rootNodes.add(node);
            } else {
                ApiPermissionNodeVO parent = nodeMap.get(node.getParentId());
                if (parent != null) {
                    parent.getChildren().add(node);
                }
                // 父节点不在启用范围内，直接丢弃
            }
        }

        // 清理空的 children 列表，设为 null
        removeEmptyChildren(rootNodes);
        return rootNodes;
    }

    /**
     * 递归清理空的 children，设为 null
     */
    private void removeEmptyChildren(List<ApiPermissionNodeVO> nodes) {
        for (ApiPermissionNodeVO node : nodes) {
            if (node.getChildren() != null && node.getChildren().isEmpty()) {
                node.setChildren(null);
            } else if (node.getChildren() != null) {
                removeEmptyChildren(node.getChildren());
            }
        }
    }

}
