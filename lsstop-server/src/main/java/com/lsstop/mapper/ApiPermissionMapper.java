package com.lsstop.mapper;

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

}
