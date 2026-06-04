package com.lsstop.service.impl;

import com.lsstop.constant.RedisConst;
import com.lsstop.domain.vo.ApiPermissionNodeVO;
import com.lsstop.mapper.ApiPermissionMapper;
import com.lsstop.service.ApiPermissionService;
import com.lsstop.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
