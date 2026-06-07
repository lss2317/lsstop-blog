package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.common.Result;
import com.lsstop.domain.dto.MenuListQueryDTO;
import com.lsstop.domain.vo.MenuAdminVO;
import com.lsstop.domain.vo.MenuVO;
import com.lsstop.enums.MenuTypeEnum;
import com.lsstop.service.MenuService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 菜单控制层
 *
 * @author lishusheng
 * @date 2026/05/04
 */
@RestController
public class MenuController {

    @Resource
    private MenuService menuService;

    /**
     * 获取当前用户的菜单列表（树形结构）
     *
     * @param request 请求对象（从拦截器中获取userId）
     * @return 树形菜单列表
     */
    @AccessLimit(seconds = 60, maxCount = 60)
    @GetMapping("/admin/menu/user")
    public Result<List<MenuVO>> getUserMenus(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        return Result.success(menuService.getUserMenuTree(userId));
    }

    /**
     * 获取全量菜单管理列表（树形结构，后台管理页面用）
     * <p>支持关键词、类型、启用状态过滤
     *
     * @param query 查询参数
     * @return 菜单管理树
     */
    @AccessLimit(seconds = 60, maxCount = 60)
    @GetMapping("/admin/menu/list")
    public Result<List<MenuAdminVO>> getAdminMenuList(MenuListQueryDTO query) {
        Integer menuType = MenuTypeEnum.toCode(query.getMenuType());
        return Result.success(menuService.getAdminMenuTree(query.getKeyword(), menuType, query.getIsEnabled()));
    }

}
