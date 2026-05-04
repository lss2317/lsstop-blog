package com.lsstop.controller;

import com.lsstop.common.Result;
import com.lsstop.domain.vo.MenuVO;
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
    @GetMapping("/admin/menu/user")
    public Result<List<MenuVO>> getUserMenus(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        return Result.success(menuService.getUserMenuTree(userId));
    }

}
