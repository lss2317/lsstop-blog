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

}
