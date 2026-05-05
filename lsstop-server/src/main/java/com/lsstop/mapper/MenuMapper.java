package com.lsstop.mapper;

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

}
