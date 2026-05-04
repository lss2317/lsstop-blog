package com.lsstop.service;

import com.lsstop.domain.vo.MenuVO;

import java.util.List;

/**
 * 菜单服务接口
 *
 * @author lishusheng
 * @date 2026/05/04
 */
public interface MenuService {

    /**
     * 获取当前用户的菜单树
     *
     * @param userId 用户uid
     * @return 树形菜单列表
     */
    List<MenuVO> getUserMenuTree(String userId);

}
