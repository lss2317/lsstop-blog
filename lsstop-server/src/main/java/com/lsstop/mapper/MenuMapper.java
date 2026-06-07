package com.lsstop.mapper;

import com.lsstop.domain.vo.MenuAdminVO;
import com.lsstop.domain.vo.MenuPermissionVO;
import com.lsstop.domain.vo.MenuVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单数据访问层
 *
 * @author lishusheng
 * @date 2026/05/04
 */
public interface MenuMapper {

    /**
     * 查询用户拥有的菜单列表（角色授权 + 用户个性化调整）
     * <p>
     * 逻辑：角色关联菜单 UNION 额外授予菜单 EXCEPT 额外排除菜单
     *
     * @param userId 用户uid
     * @return 扁平菜单列表
     */
    List<MenuVO> selectMenusByUserId(@Param("userId") String userId);

    /**
     * 查询系统所有启用的按钮权限的 path（menuType=3）
     * <p>用于拦截器判断接口是否受权限管控
     *
     * @return 所有按钮权限的 path 列表（格式：METHOD:/uri/pattern）
     */
    List<String> selectAllButtonPaths();

    /**
     * 根据ID列表批量查询菜单（用于补全祖先目录）
     *
     * @param ids 菜单ID列表
     * @return 菜单列表
     */
    List<MenuVO> selectMenusByIds(@Param("ids") List<Integer> ids);

    /**
     * 查询系统所有启用的菜单（精简字段，用于权限配置弹窗）
     *
     * @return 全量启用菜单列表（已按 sort, id 排序）
     */
    List<MenuPermissionVO> selectAllMenusForPermission();

    /**
     * 查询所有菜单（管理后台用，支持搜索过滤）
     *
     * @param keyword   关键词（模糊搜索：名称、路由、权限标识）
     * @param menuType  菜单类型（1-目录 2-菜单 3-按钮 4-内嵌 5-外链）
     * @param isEnabled 是否启用（0-禁用 1-启用）
     * @return 过滤后的菜单列表（已按 sort, id 排序）
     */
    List<MenuAdminVO> selectAllMenus(@Param("keyword") String keyword,
                                     @Param("menuType") Integer menuType,
                                     @Param("isEnabled") Integer isEnabled);

    /**
     * 根据ID列表查询菜单（管理后台用，不过滤 is_enabled，用于补全祖先目录）
     *
     * @param ids 菜单ID列表
     * @return 菜单列表（含 isEnabled、createTime、updateTime）
     */
    List<MenuAdminVO> selectAdminMenusByIds(@Param("ids") List<Integer> ids);

}
