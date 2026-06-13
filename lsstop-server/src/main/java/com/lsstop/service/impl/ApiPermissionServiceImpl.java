package com.lsstop.service.impl;

import com.lsstop.constant.ApiPermissionConst;
import com.lsstop.constant.CommonConst;
import com.lsstop.constant.RedisConst;
import com.lsstop.domain.dto.AddApiPermissionDTO;
import com.lsstop.domain.dto.UpdateApiPermissionDTO;
import com.lsstop.domain.entity.ApiPermissionEntity;
import com.lsstop.domain.vo.ApiPermissionAdminVO;
import com.lsstop.domain.vo.ApiPermissionNodeVO;
import com.lsstop.enums.RequestMethodEnum;
import com.lsstop.enums.StatusEnum;
import com.lsstop.exception.BusinessException;
import com.lsstop.mapper.ApiPermissionMapper;
import com.lsstop.service.ApiPermissionService;
import com.lsstop.utils.RedisUtils;
import com.lsstop.utils.StringUtils;
import com.lsstop.utils.ValidateUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
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
            if (node.getParentId() == null || node.getParentId() == ApiPermissionConst.TOP_LEVEL_PARENT_ID) {
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
                .filter(p -> p != null && p != ApiPermissionConst.TOP_LEVEL_PARENT_ID)
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
                if (parent.getParentId() != null && parent.getParentId() != ApiPermissionConst.TOP_LEVEL_PARENT_ID
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
            if (node.getParentId() == null || node.getParentId() == ApiPermissionConst.TOP_LEVEL_PARENT_ID) {
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

    @Override
    public void addApiPermission(AddApiPermissionDTO dto) {
        dto.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : null);
        int parentId = dto.getParentId() != null ? dto.getParentId() : ApiPermissionConst.TOP_LEVEL_PARENT_ID;
        ApiPermissionEntity entity = validateAndBuildEntity(dto, parentId, null);
        apiPermissionMapper.insertApiPermission(entity);
        clearApiPermissionCache();
    }

    @Override
    public void updateApiPermission(UpdateApiPermissionDTO dto) {
        // 1. 校验权限是否存在
        ApiPermissionEntity existing = apiPermissionMapper.selectApiPermissionFullById(dto.getId());
        if (existing == null) {
            throw new BusinessException(StatusEnum.NOT_FOUND, ApiPermissionConst.API_PERMISSION_NOT_FOUND);
        }

        dto.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : null);

        // 2. 类型不变性校验：目录↔接口不允许互转
        boolean newIsDirectory = StringUtils.isBlank(dto.getRequestMethod())
                && StringUtils.isBlank(dto.getRequestUrl());
        boolean oldIsDirectory = StringUtils.isBlank(existing.getRequestMethod())
                && StringUtils.isBlank(existing.getRequestUrl());
        if (newIsDirectory != oldIsDirectory) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(),
                    ApiPermissionConst.TYPE_IMMUTABLE);
        }

        // 3. 父级ID归一化
        int parentId = dto.getParentId() != null ? dto.getParentId() : existing.getParentId();

        // 4. 若parentId变更，校验循环引用
        if (parentId != existing.getParentId()) {
            validateNoCircularReference(dto.getId(), parentId);
        }

        // 5. 校验+构建（复用AddDTO的校验逻辑）
        AddApiPermissionDTO addDto = toAddDTO(dto);
        ApiPermissionEntity entity = validateAndBuildEntity(addDto, parentId, dto.getId());
        entity.setId(dto.getId());
        apiPermissionMapper.updateApiPermission(entity);

        clearApiPermissionCache();
    }

    @Override
    public void deleteApiPermission(Integer id) {
        ApiPermissionEntity entity = apiPermissionMapper.selectApiPermissionById(id);
        if (entity == null) {
            throw new BusinessException(StatusEnum.NOT_FOUND, ApiPermissionConst.API_PERMISSION_NOT_FOUND);
        }

        Integer childCount = apiPermissionMapper.countByParentId(id);
        if (childCount != null && childCount > 0) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), ApiPermissionConst.HAS_CHILDREN);
        }

        apiPermissionMapper.deleteById(id, System.currentTimeMillis());
        clearApiPermissionCache();
    }

    /**
     * 校验字段格式 + 构建实体
     *
     * @param dto       新增参数（新增直接传入，编辑通过 toAddDTO 转换）
     * @param parentId  父级ID（已归一化）
     * @param excludeId 排除的权限ID（新增为null，编辑为当前ID）
     * @return 接口权限实体（不含id）
     */
    private ApiPermissionEntity validateAndBuildEntity(AddApiPermissionDTO dto, int parentId,
                                                       Integer excludeId) {
        // 校验权限描述
        if (StringUtils.isBlank(dto.getDescription())) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(),
                    ApiPermissionConst.DESCRIPTION_REQUIRED);
        }

        // 判断节点类型并校验
        boolean isDirectory = StringUtils.isBlank(dto.getRequestMethod())
                && StringUtils.isBlank(dto.getRequestUrl());
        if (isDirectory) {
            validateDirectoryNode(parentId);
        } else {
            validateApiNode(dto, parentId, excludeId);
        }

        // 构建实体
        return buildEntity(dto, parentId);
    }

    /**
     * 将UpdateApiPermissionDTO转为AddApiPermissionDTO，复用校验逻辑
     */
    private AddApiPermissionDTO toAddDTO(UpdateApiPermissionDTO dto) {
        AddApiPermissionDTO addDto = new AddApiPermissionDTO();
        addDto.setParentId(dto.getParentId());
        addDto.setDescription(dto.getDescription());
        addDto.setRequestMethod(dto.getRequestMethod());
        addDto.setRequestUrl(dto.getRequestUrl());
        addDto.setSort(dto.getSort());
        addDto.setIsEnabled(dto.getIsEnabled());
        return addDto;
    }

    /**
     * 校验目录节点
     * <p>非顶级时父级必须是目录
     */
    private void validateDirectoryNode(int parentId) {
        if (parentId != ApiPermissionConst.TOP_LEVEL_PARENT_ID) {
            validateParentIsDirectory(parentId, ApiPermissionConst.DIRECTORY_MUST_UNDER_DIRECTORY);
        }
    }

    /**
     * 校验接口节点
     */
    private void validateApiNode(AddApiPermissionDTO dto, int parentId, Integer excludeId) {
        // 请求方法校验
        if (StringUtils.isBlank(dto.getRequestMethod())) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(),
                    ApiPermissionConst.REQUEST_METHOD_REQUIRED);
        }
        validateRequestMethod(dto.getRequestMethod());

        // 接口路径校验
        if (StringUtils.isBlank(dto.getRequestUrl())) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(),
                    ApiPermissionConst.REQUEST_URL_REQUIRED);
        }
        validateRequestUrlFormat(dto.getRequestUrl());

        // 非顶级时父级必须是目录
        if (parentId != ApiPermissionConst.TOP_LEVEL_PARENT_ID) {
            validateParentIsDirectory(parentId, ApiPermissionConst.API_MUST_UNDER_DIRECTORY);
        }

        // requestUrl + requestMethod 全局唯一性校验
        Integer count = apiPermissionMapper.countByUrlAndMethod(
                dto.getRequestUrl().trim(),
                dto.getRequestMethod().toUpperCase().trim(), excludeId);
        if (count != null && count > 0) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(),
                    ApiPermissionConst.SAME_LEVEL_REQUEST_URL_EXISTS);
        }
    }

    /**
     * 校验请求方法合法性
     */
    private void validateRequestMethod(String method) {
        if (!RequestMethodEnum.isValid(method)) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(),
                    ApiPermissionConst.INVALID_REQUEST_METHOD);
        }
    }

    /**
     * 校验接口路径格式
     */
    private void validateRequestUrlFormat(String requestUrl) {
        if (requestUrl == null || !requestUrl.startsWith("/")) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(),
                    ApiPermissionConst.REQUEST_URL_MUST_START_WITH_SLASH);
        }
        if (!ValidateUtils.isValidRequestUrl(requestUrl)) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(),
                    ApiPermissionConst.REQUEST_URL_FORMAT_INVALID);
        }
    }

    /**
     * 校验父级必须是目录节点
     *
     * @param parentId     父级ID
     * @param errorMessage 校验失败时的错误提示
     */
    private void validateParentIsDirectory(int parentId, String errorMessage) {
        ApiPermissionEntity parent = apiPermissionMapper.selectApiPermissionById(parentId);
        if (parent == null) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(),
                    ApiPermissionConst.PARENT_NOT_FOUND);
        }
        boolean parentIsDirectory = StringUtils.isBlank(parent.getRequestMethod());
        if (!parentIsDirectory) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), errorMessage);
        }
    }

    /**
     * 校验循环引用
     */
    private void validateNoCircularReference(int id, int parentId) {
        if (parentId == id) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(),
                    ApiPermissionConst.CIRCULAR_REFERENCE);
        }
        int currentId = parentId;
        while (currentId != ApiPermissionConst.TOP_LEVEL_PARENT_ID) {
            ApiPermissionEntity ancestor = apiPermissionMapper.selectApiPermissionById(currentId);
            if (ancestor == null) {
                break;
            }
            currentId = ancestor.getParentId() != null ? ancestor.getParentId() : ApiPermissionConst.TOP_LEVEL_PARENT_ID;
            if (currentId == id) {
                throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(),
                        ApiPermissionConst.CIRCULAR_REFERENCE);
            }
        }
    }

    /**
     * 构建实体（新增/编辑共用）
     * <p>仅负责字段映射和默认值填充，不做任何校验
     *
     * @param dto      新增参数（编辑时通过 toAddDTO 转换而来）
     * @param parentId 父级ID（已归一化）
     * @return 接口权限实体
     */
    private ApiPermissionEntity buildEntity(AddApiPermissionDTO dto, int parentId) {
        int sort = dto.getSort() != null ? dto.getSort() : ApiPermissionConst.DEFAULT_SORT;
        int isEnabled = dto.getIsEnabled() != null ? dto.getIsEnabled() : CommonConst.ENABLED;
        String requestUrl = StringUtils.isBlank(dto.getRequestUrl()) ? null : dto.getRequestUrl().trim();
        String requestMethod = StringUtils.isBlank(dto.getRequestMethod()) ? null : dto.getRequestMethod().toUpperCase().trim();

        return ApiPermissionEntity.builder()
                .parentId(parentId)
                .description(dto.getDescription())
                .requestUrl(requestUrl)
                .requestMethod(requestMethod)
                .sort(sort)
                .isEnabled(isEnabled)
                .build();
    }

    /**
     * 清理接口权限相关Redis缓存
     */
    private void clearApiPermissionCache() {
        redisUtils.delete(RedisConst.API_PERMISSION_TREE);
        redisUtils.delete(RedisConst.REGISTERED_API_PERMISSIONS);
        // 权限变更可能影响所有用户，清除所有用户有效权限缓存
        redisUtils.deleteByPrefix(RedisConst.USER_EFFECTIVE_API_PERMISSIONS);
    }

    @Override
    public Set<String> getUserEffectiveApiPermissions(String userId) {
        String cacheKey = RedisConst.USER_EFFECTIVE_API_PERMISSIONS + userId;
        // 先从缓存获取
        Set<String> cachedPermissions = redisUtils.getSet(cacheKey, String.class);
        if (cachedPermissions != null) {
            return cachedPermissions;
        }
        // 缓存不存在，查询数据库
        List<ApiPermissionEntity> entities = apiPermissionMapper.selectUserEffectiveApiPermissions(userId);
        Set<String> permissions = new HashSet<>();
        for (ApiPermissionEntity entity : entities) {
            if (entity.getRequestUrl() != null && entity.getRequestMethod() != null) {
                permissions.add(entity.getRequestMethod() + ":" + entity.getRequestUrl());
            }
        }
        // 写入缓存，过期时间1天
        redisUtils.set(cacheKey, permissions, RedisConst.EXPIRE_ONE_DAY);
        return permissions;
    }

    @Override
    public Set<String> getAllRegisteredApiPermissions() {
        String cacheKey = RedisConst.REGISTERED_API_PERMISSIONS;
        // 先从缓存获取
        Set<String> cachedPermissions = redisUtils.getSet(cacheKey, String.class);
        if (cachedPermissions != null) {
            return cachedPermissions;
        }
        // 缓存不存在，查询数据库
        List<ApiPermissionEntity> entities = apiPermissionMapper.selectAllEnabledApiPatterns();
        Set<String> permissions = new HashSet<>();
        for (ApiPermissionEntity entity : entities) {
            if (entity.getRequestUrl() != null && entity.getRequestMethod() != null) {
                permissions.add(entity.getRequestMethod() + ":" + entity.getRequestUrl());
            }
        }
        // 写入缓存，过期时间1天
        redisUtils.set(cacheKey, permissions, RedisConst.EXPIRE_ONE_DAY);
        return permissions;
    }

}
