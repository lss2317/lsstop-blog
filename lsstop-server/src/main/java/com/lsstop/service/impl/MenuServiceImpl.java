package com.lsstop.service.impl;

import com.lsstop.domain.vo.MenuVO;
import com.lsstop.mapper.MenuMapper;
import com.lsstop.service.MenuService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 菜单服务实现类
 *
 * @author lishusheng
 * @date 2026/05/04
 */
@Service
public class MenuServiceImpl implements MenuService {

    @Resource
    private MenuMapper menuMapper;

    /**
     * 获取当前用户的菜单树
     *
     * @param userId 用户uid
     * @return 树形菜单列表
     */
    @Override
    public List<MenuVO> getUserMenuTree(String userId) {
        List<MenuVO> flatMenus = menuMapper.selectMenusByUserId(userId);
        return buildTree(flatMenus);
    }

    /**
     * 将扁平菜单列表构建为树形结构
     *
     * @param flatMenus 扁平菜单列表（已按 sort, id 排序）
     * @return 树形菜单列表
     */
    private List<MenuVO> buildTree(List<MenuVO> flatMenus) {
        // 使用 LinkedHashMap 保持插入顺序（即排序顺序）
        Map<Integer, MenuVO> menuMap = new LinkedHashMap<>();
        for (MenuVO menu : flatMenus) {
            menu.setChildren(new ArrayList<>());
            menuMap.put(menu.getId(), menu);
        }

        List<MenuVO> rootMenus = new ArrayList<>();
        for (MenuVO menu : flatMenus) {
            if (menu.getParentId() == null || menu.getParentId() == 0) {
                rootMenus.add(menu);
            } else {
                MenuVO parent = menuMap.get(menu.getParentId());
                if (parent != null) {
                    parent.getChildren().add(menu);
                }
                // 父节点不在权限范围内，直接丢弃（无入口可达）
            }
        }

        // 清理空的 children 列表，设为 null
        removeEmptyChildren(rootMenus);
        return rootMenus;
    }

    /**
     * 递归清理空的 children，设为 null
     */
    private void removeEmptyChildren(List<MenuVO> menus) {
        for (MenuVO menu : menus) {
            if (menu.getChildren() != null && menu.getChildren().isEmpty()) {
                menu.setChildren(null);
            } else if (menu.getChildren() != null) {
                removeEmptyChildren(menu.getChildren());
            }
        }
    }

}
