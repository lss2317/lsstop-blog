package com.lsstop.controller;

import com.lsstop.common.Result;
import com.lsstop.constant.CommentConst;
import com.lsstop.domain.dto.BindCodeDTO;
import com.lsstop.domain.dto.ChangeEmailDTO;
import com.lsstop.domain.dto.ChangePasswordDTO;
import com.lsstop.domain.dto.UpdateUserInfoDTO;
import com.lsstop.domain.vo.UserProfileVO;
import com.lsstop.domain.vo.UserPublicProfileVO;
import com.lsstop.domain.vo.UserInfoVO;
import com.lsstop.domain.vo.UserRecentCommentVO;
import com.lsstop.service.AuthService;
import com.lsstop.service.CommentService;
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

import java.util.List;

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

    @Resource
    private CommentService commentService;

    /**
     * 获取当前登录用户信息
     *
     * @param request 请求对象（拦截器已验证token并存入userId）
     * @return 用户资料信息
     */
    @GetMapping("/front/user/me")
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

    /**
     * 更新用户信息
     *
     * @param request 请求对象（拦截器已验证token并存入userId）
     * @param dto     更新用户信息参数
     * @return 操作结果
     */
    @PostMapping("/front/user/info")
    public Result<Void> updateUserInfo(HttpServletRequest request, @RequestBody @Validated UpdateUserInfoDTO dto) {
        String userId = (String) request.getAttribute("userId");
        userService.updateUserInfo(userId, dto);
        return Result.success();
    }

    /**
     * 修改密码
     *
     * @param request 请求对象（拦截器已验证token并存入userId）
     * @param dto     修改密码参数
     * @return 操作结果
     */
    @PostMapping("/front/user/password")
    public Result<Void> changePassword(HttpServletRequest request, @RequestBody @Validated ChangePasswordDTO dto) {
        String userId = (String) request.getAttribute("userId");
        authService.changePassword(userId, dto);
        return Result.success();
    }

    /**
     * 获取指定用户最近评论
     *
     * @param userId 用户ID
     * @return 用户最近评论列表（最多10条）
     */
    @GetMapping("/front/user/recentComments/{userId}")
    public Result<List<UserRecentCommentVO>> getRecentComments(@PathVariable String userId) {
        List<UserRecentCommentVO> comments = commentService.getRecentComments(userId, CommentConst.RECENT_COMMENT_LIMIT);
        return Result.success(comments);
    }

    /**
     * 绑定QQ
     *
     * @param request 请求对象（拦截器已验证token并存入userId）
     * @param dto     绑定参数
     * @return 操作结果
     */
    @PostMapping("/front/user/bind/qq")
    public Result<Void> bindQQ(HttpServletRequest request, @RequestBody @Validated BindCodeDTO dto) {
        String userId = (String) request.getAttribute("userId");
        authService.bindQQ(userId, dto);
        return Result.success();
    }

    /**
     * 绑定微博
     *
     * @param request 请求对象（拦截器已验证token并存入userId）
     * @param dto     绑定参数
     * @return 操作结果
     */
    @PostMapping("/front/user/bind/weibo")
    public Result<Void> bindWeibo(HttpServletRequest request, @RequestBody @Validated BindCodeDTO dto) {
        String userId = (String) request.getAttribute("userId");
        authService.bindWeibo(userId, dto);
        return Result.success();
    }

}
