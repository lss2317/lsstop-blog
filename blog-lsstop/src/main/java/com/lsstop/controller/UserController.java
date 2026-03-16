package com.lsstop.controller;

import com.lsstop.common.Result;
import com.lsstop.domain.dto.ChangeEmailDTO;
import com.lsstop.domain.vo.UserProfileVO;
import com.lsstop.domain.vo.UserPublicProfileVO;
import com.lsstop.domain.vo.UserInfoVO;
import com.lsstop.service.AuthService;
import com.lsstop.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @Resource
    private AuthService authService;

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

    /**
     * 获取用户公开主页详情（查看他人）
     *
     * @param userId 用户ID
     * @return 用户公开主页详情
     */
    @GetMapping("/front/user/profile/{userId}")
    public Result<UserPublicProfileVO> getUserHomeDetail(@PathVariable String userId) {
        return Result.success(userService.getUserHomeDetail(userId));
    }

    /**
     * 获取当前用户主页详情（查看自己）
     *
     * @param request 请求对象（拦截器已验证token并存入userId）
     * @return 用户完整主页详情
     */
    @GetMapping("/front/user/profile")
    public Result<UserProfileVO> getMyHomeDetail(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        return Result.success(userService.getMyHomeDetail(userId));
    }

    /**
     * 修改绑定邮箱
     *
     * @param request 请求对象（拦截器已验证token并存入userId）
     * @param dto     修改邮箱参数
     * @return 操作结果
     */
    @PostMapping("/front/user/email")
    public Result<Void> changeEmail(HttpServletRequest request, @RequestBody @Validated ChangeEmailDTO dto) {
        String userId = (String) request.getAttribute("userId");
        authService.changeEmail(userId, dto);
        return Result.success();
    }

    /**
     * 更新用户头像
     *
     * @param request 请求对象（拦截器已验证token并存入userId）
     * @param file    头像文件
     * @return 新头像URL
     */
    @PostMapping("/front/user/avatar")
    public Result<String> updateAvatar(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        String userId = (String) request.getAttribute("userId");
        String avatarUrl = userService.updateAvatar(userId, file);
        return Result.success(avatarUrl);
    }

}
