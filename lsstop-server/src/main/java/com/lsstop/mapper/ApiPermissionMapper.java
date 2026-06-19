package com.lsstop.mapper;

import com.lsstop.domain.entity.ApiPermissionEntity;
import com.lsstop.domain.vo.ApiPermissionAdminVO;
import com.lsstop.domain.vo.ApiPermissionNodeVO;
import org.apache.ibatis.annotations.Param;

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

    /**
     * 查询接口权限列表（后台管理用）
     * <p>支持关键词、请求方法、启用状态过滤
     *
     * @param keyword       关键词（模糊搜索：描述、接口路径）
     * @param requestMethod 请求方法
     * @param isEnabled     是否启用
     * @return 接口权限列表
     */
    List<ApiPermissionAdminVO> selectApiPermissions(@Param("keyword") String keyword,
                                                     @Param("requestMethod") String requestMethod,
                                                     @Param("isEnabled") Integer isEnabled);

    /**
     * 根据ID列表批量查询接口权限（不过滤 is_enabled，用于补全祖先）
     *
     * @param ids 接口权限ID列表
     * @return 接口权限列表（含 isEnabled、createTime、updateTime）
     */
    List<ApiPermissionAdminVO> selectApiPermissionsByIds(@Param("ids") List<Integer> ids);

    /**
     * 新增接口权限
     *
     * @param entity 接口权限数据
     */
    void insertApiPermission(ApiPermissionEntity entity);

    /**
     * 更新接口权限
     *
     * @param entity 接口权限数据
     */
    void updateApiPermission(ApiPermissionEntity entity);

    /**
     * 软删除接口权限
     *
     * @param id        权限ID
     * @param deletedAt 删除时间戳
     */
    void deleteById(@Param("id") Integer id, @Param("deletedAt") Long deletedAt);

    /**
     * 根据ID查询接口权限（校验用，仅返回 id、parentId、requestMethod）
     *
     * @param id 权限ID
     * @return 接口权限实体（精简字段）
     */
    ApiPermissionEntity selectApiPermissionById(@Param("id") Integer id);

    /**
     * 根据ID查询接口权限完整信息（编辑回显用）
     *
     * @param id 权限ID
     * @return 接口权限完整实体
     */
    ApiPermissionEntity selectApiPermissionFullById(@Param("id") Integer id);

    /**
     * 统计指定父级下的子权限数量
     *
     * @param parentId 父级ID
     * @return 子权限数量
     */
    Integer countByParentId(@Param("parentId") Integer parentId);

    /**
     * 校验(requestUrl, requestMethod)组合是否已存在（全局唯一）
     *
     * @param requestUrl    接口路径
     * @param requestMethod 请求方法（GET/POST/PUT/DELETE）
     * @param excludeId     排除的权限ID（编辑时传当前ID，新增时传null）
     * @return 存在返回数量 > 0
     */
    Integer countByUrlAndMethod(@Param("requestUrl") String requestUrl,
                                 @Param("requestMethod") String requestMethod,
                                 @Param("excludeId") Integer excludeId);

    /**
     * 查询用户有效接口权限（角色授予 ∪ 用户额外授予 - 用户额外排除）
     * <p>仅返回接口节点（request_url IS NOT NULL），不含目录
     *
     * @param userId 用户uid
     * @return 用户有效接口权限列表（含 request_url 和 request_method）
     */
    List<ApiPermissionEntity> selectUserEffectiveApiPermissions(@Param("userId") String userId);

    /**
     * 查询用户有效接口权限ID列表（角色授予 ∪ 用户额外授予 - 用户额外排除）
     *
     * @param userId 用户uid
     * @return 接口权限ID列表
     */
    List<Integer> selectUserEffectiveApiPermissionIds(@Param("userId") String userId);

    /**
     * 查询用户通过角色继承的接口权限ID列表（仅角色授权，不含用户个性化调整）
     *
     * @param userId 用户uid
     * @return 接口权限ID列表
     */
    List<Integer> selectRoleApiPermissionIdsByUserId(@Param("userId") String userId);

    /**
     * 查询用户指定类型的接口权限调整ID列表
     *
     * @param userId 用户uid
     * @param type   调整类型：1-额外授予 2-额外排除
     * @return 接口权限ID列表
     */
    List<Integer> selectUserApiPermissionIdsByType(@Param("userId") String userId,
                                                     @Param("type") Integer type);

    /**
     * 批量新增用户接口权限关联（已存在活跃记录时幂等更新）
     *
     * @param userId           用户ID
     * @param apiPermissionIds 接口权限ID列表
     * @param type             调整类型：1-额外授予 2-额外排除
     */
    void batchInsertUserApiPermission(@Param("userId") String userId,
                                       @Param("apiPermissionIds") List<Integer> apiPermissionIds,
                                       @Param("type") Integer type);

    /**
     * 批量软删除用户指定类型的接口权限关联
     *
     * @param userId           用户ID
     * @param apiPermissionIds 接口权限ID列表
     * @param type             调整类型：1-额外授予 2-额外排除
     * @param deletedAt        删除时间戳
     */
    void batchSoftDeleteUserApiPermission(@Param("userId") String userId,
                                           @Param("apiPermissionIds") List<Integer> apiPermissionIds,
                                           @Param("type") Integer type,
                                           @Param("deletedAt") Long deletedAt);

    /**
     * 查询系统所有启用的接口权限模式（用于判断 URL 是否受控）
     * <p>仅返回接口节点（request_url IS NOT NULL），不含目录
     *
     * @return 全量启用接口权限列表（含 request_url 和 request_method）
     */
    List<ApiPermissionEntity> selectAllEnabledApiPatterns();

}
