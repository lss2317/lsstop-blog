package com.lsstop.controller;

import com.lsstop.common.Result;
import com.lsstop.domain.vo.UserInfoVO;
import com.lsstop.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户控制器
 *
 * @author lishusheng
 * @date 2026/01/03
 */
@RestController
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 获取当前登录用户信息
     *
     * @param request 请求对象（拦截器已验证token并存入userId）
     * @return 用户资料信息
     */
    @GetMapping("/front/user/info")
    public Result<UserInfoVO> getUserInfo(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        return Result.success(userService.getUserProfile(userId));
    }

}
