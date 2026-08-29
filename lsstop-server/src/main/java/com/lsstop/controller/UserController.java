package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.annotation.OperationLog;
import com.lsstop.common.Result;
import com.lsstop.constant.CommentConst;
import com.lsstop.domain.dto.AddUserDTO;
import com.lsstop.domain.dto.AdminResetPasswordDTO;
import com.lsstop.domain.dto.BindCodeDTO;
import com.lsstop.domain.dto.ChangeEmailDTO;
import com.lsstop.domain.dto.AdminUpdateProfileDTO;
import com.lsstop.domain.dto.ChangePasswordDTO;
import com.lsstop.domain.dto.UpdateUserApiPermissionDTO;
import com.lsstop.domain.dto.UpdateUserDTO;
import com.lsstop.domain.dto.UpdateUserInfoDTO;
import com.lsstop.domain.dto.UpdateUserMenuDTO;
import com.lsstop.domain.vo.AdminUserInfoVO;
import com.lsstop.domain.vo.UserPageVO;
import com.lsstop.domain.vo.UserProfileVO;
import com.lsstop.domain.vo.UserPublicProfileVO;
import com.lsstop.domain.vo.UserInfoVO;
import com.lsstop.domain.vo.UserRecentCommentVO;
import com.lsstop.enums.OperationModuleEnum;
import com.lsstop.enums.OperationTypeEnum;
import com.lsstop.enums.StatusEnum;
import com.lsstop.exception.BusinessException;
import com.lsstop.service.AuthService;
import com.lsstop.service.CommentService;
import com.lsstop.service.ApiPermissionService;
import com.lsstop.service.MenuService;
import com.lsstop.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @Resource
    private MenuService menuService;

    @Resource
    private ApiPermissionService apiPermissionService;

    /**
     * 获取当前登录用户信息
     *
     * @param request 请求对象（拦截器已验证token并存入userId）
     * @return 用户资料信息
     */
    @GetMapping("/front/user/me")
    @AccessLimit(seconds = 60, maxCount = 60)
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
    @AccessLimit(seconds = 60, maxCount = 60)
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
    @AccessLimit(seconds = 60, maxCount = 60)
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
    @AccessLimit(seconds = 60, maxCount = 10)
    @OperationLog(module = OperationModuleEnum.USER, type = OperationTypeEnum.UPDATE, description = "修改邮箱")
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
    @AccessLimit(seconds = 60, maxCount = 10)
    @OperationLog(module = OperationModuleEnum.USER, type = OperationTypeEnum.UPDATE, description = "修改头像")
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
    @AccessLimit(seconds = 60, maxCount = 10)
    @OperationLog(module = OperationModuleEnum.USER, type = OperationTypeEnum.UPDATE, description = "修改个人信息")
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
    @AccessLimit(seconds = 60, maxCount = 5)
    @OperationLog(module = OperationModuleEnum.USER, type = OperationTypeEnum.UPDATE, description = "修改密码")
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
    @AccessLimit(seconds = 60, maxCount = 60)
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
    @AccessLimit(seconds = 60, maxCount = 10)
    @OperationLog(module = OperationModuleEnum.USER, type = OperationTypeEnum.UPDATE, description = "绑定QQ")
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
    @AccessLimit(seconds = 60, maxCount = 10)
    @OperationLog(module = OperationModuleEnum.USER, type = OperationTypeEnum.UPDATE, description = "绑定微博")
    public Result<Void> bindWeibo(HttpServletRequest request, @RequestBody @Validated BindCodeDTO dto) {
        String userId = (String) request.getAttribute("userId");
        authService.bindWeibo(userId, dto);
        return Result.success();
    }

    /**
     * 解绑QQ
     *
     * @param request 请求对象（拦截器已验证token并存入userId）
     * @return 操作结果
     */
    @PostMapping("/front/user/unbind/qq")
    @AccessLimit(seconds = 60, maxCount = 10)
    @OperationLog(module = OperationModuleEnum.USER, type = OperationTypeEnum.UPDATE, description = "解绑QQ")
    public Result<Void> unbindQQ(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        authService.unbindQQ(userId);
        return Result.success();
    }

    /**
     * 解绑微博
     *
     * @param request 请求对象（拦截器已验证token并存入userId）
     * @return 操作结果
     */
    @PostMapping("/front/user/unbind/weibo")
    @AccessLimit(seconds = 60, maxCount = 10)
    @OperationLog(module = OperationModuleEnum.USER, type = OperationTypeEnum.UPDATE, description = "解绑微博")
    public Result<Void> unbindWeibo(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        authService.unbindWeibo(userId);
        return Result.success();
    }

    /**
     * 后台新增用户
     *
     * @param dto 新增用户参数
     */
    @PostMapping("/admin/user/add")
    @AccessLimit(seconds = 60, maxCount = 10)
    @OperationLog(module = OperationModuleEnum.USER, type = OperationTypeEnum.ADD, description = "新增用户")
    public Result<Void> addUser(@RequestBody @Validated AddUserDTO dto) {
        userService.addUser(dto);
        return Result.success();
    }

    /**
     * 后台更新用户
     *
     * @param dto 更新用户参数
     * @return 操作结果
     */
    @PutMapping("/admin/user/update")
    @AccessLimit(seconds = 60, maxCount = 10)
    @OperationLog(module = OperationModuleEnum.USER, type = OperationTypeEnum.UPDATE, description = "更新用户")
    public Result<Void> updateUser(@RequestBody @Validated UpdateUserDTO dto) {
        userService.updateUser(dto);
        return Result.success();
    }

    /**
     * 获取用户管理列表（分页）
     *
     * @param current  当前页码
     * @param size     每页条数
     * @param userId   用户ID（精确匹配）
     * @param nickname 昵称（模糊搜索）
     * @param email    邮箱（模糊搜索）
     * @param status   状态（0-禁用 1-正常）
     * @return 用户列表及总数
     */
    @GetMapping("/admin/user/list")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<UserPageVO> listUser(@RequestParam Integer current,
                                       @RequestParam Integer size,
                                       @RequestParam(required = false) String userId,
                                       @RequestParam(required = false) String nickname,
                                       @RequestParam(required = false) String email,
                                       @RequestParam(required = false) Integer status) {
        if (current < 1) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), CommentConst.INVALID_PAGE_PARAM);
        }
        if (size < 1) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), CommentConst.INVALID_PAGE_PARAM);
        }
        UserPageVO pageVO = new UserPageVO(
                userService.listUsers(current, size, userId, nickname, email, status),
                current, size, userService.countUserTotal(userId, nickname, email, status)
        );
        return Result.success(pageVO);
    }

    /**
     * 获取后台当前登录用户信息
     *
     * @param request 请求对象（拦截器已验证token并存入userId）
     * @return 后台用户信息
     */
    @GetMapping("/admin/user/info")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<AdminUserInfoVO> getAdminUserInfo(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        return Result.success(userService.getAdminUserInfo(userId));
    }

    /**
     * 获取用户有效菜单权限ID列表
     *
     * @param userId 用户ID
     * @return 菜单ID列表
     */
    @GetMapping("/admin/user/menu-permission")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<List<Integer>> getUserMenuPermission(@RequestParam String userId) {
        return Result.success(menuService.getUserMenuIds(userId));
    }

    /**
     * 修改用户菜单权限
     * <p>接收全量菜单ID列表，后端与现有权限做差集计算后更新
     *
     * @param dto 修改用户菜单权限参数
     * @return 操作结果
     */
    @PutMapping("/admin/user/menu-permission")
    @AccessLimit(seconds = 60, maxCount = 30)
    @OperationLog(module = OperationModuleEnum.USER, type = OperationTypeEnum.AUTHORIZATION, description = "修改用户菜单权限")
    public Result<Void> updateUserMenuPermission(@RequestBody @Validated UpdateUserMenuDTO dto) {
        userService.updateUserMenuPermission(dto);
        return Result.success();
    }

    /**
     * 获取用户有效接口权限ID列表
     *
     * @param userId 用户ID
     * @return 接口权限ID列表
     */
    @GetMapping("/admin/user/api-permission")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<List<Integer>> getUserApiPermission(@RequestParam String userId) {
        return Result.success(apiPermissionService.getUserEffectiveApiPermissionIds(userId));
    }

    /**
     * 修改用户接口权限
     * <p>接收全量接口权限ID列表，后端与角色接口权限做差集计算后更新
     *
     * @param dto 修改用户接口权限参数
     * @return 操作结果
     */
    @PutMapping("/admin/user/api-permission")
    @AccessLimit(seconds = 60, maxCount = 30)
    @OperationLog(module = OperationModuleEnum.USER, type = OperationTypeEnum.AUTHORIZATION, description = "修改用户接口权限")
    public Result<Void> updateUserApiPermission(@RequestBody @Validated UpdateUserApiPermissionDTO dto) {
        userService.updateUserApiPermission(dto);
        return Result.success();
    }

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/admin/user/delete/{userId}")
    @AccessLimit(seconds = 60, maxCount = 10)
    @OperationLog(module = OperationModuleEnum.USER, type = OperationTypeEnum.DELETE, description = "删除用户")
    public Result<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return Result.success();
    }

    /**
     * 后台重置用户密码
     *
     * @param dto 重置密码请求参数
     * @return 操作结果
     */
    @PutMapping("/admin/user/reset-password")
    @AccessLimit(seconds = 60, maxCount = 10)
    @OperationLog(module = OperationModuleEnum.USER, type = OperationTypeEnum.RESET, description = "重置用户密码")
    public Result<Void> resetPassword(@RequestBody @Validated AdminResetPasswordDTO dto) {
        authService.adminResetPassword(dto);
        return Result.success();
    }

    /**
     * 后台修改当前登录用户密码（需旧密码验证）
     *
     * @param request 请求对象（拦截器已验证token并存入userId）
     * @param dto     修改密码参数
     * @return 操作结果
     */
    @PutMapping("/admin/user/change-password")
    @AccessLimit(seconds = 60, maxCount = 5)
    @OperationLog(module = OperationModuleEnum.USER, type = OperationTypeEnum.UPDATE, description = "后台修改密码")
    public Result<Void> adminChangePassword(HttpServletRequest request, @RequestBody @Validated ChangePasswordDTO dto) {
        String userId = (String) request.getAttribute("userId");
        authService.changePassword(userId, dto);
        return Result.success();
    }

    /**
     * 后台修改当前登录用户邮箱
     *
     * @param request 请求对象（拦截器已验证token并存入userId）
     * @param dto     修改邮箱参数
     * @return 操作结果
     */
    @PutMapping("/admin/user/email")
    @AccessLimit(seconds = 60, maxCount = 10)
    @OperationLog(module = OperationModuleEnum.USER, type = OperationTypeEnum.UPDATE, description = "后台修改邮箱")
    public Result<Void> adminChangeEmail(HttpServletRequest request, @RequestBody @Validated ChangeEmailDTO dto) {
        String userId = (String) request.getAttribute("userId");
        authService.changeEmail(userId, dto);
        return Result.success();
    }

    /**
     * 后台个人中心更新个人资料
     *
     * @param request 请求对象（拦截器已验证token并存入userId）
     * @param dto     更新资料参数
     * @return 操作结果
     */
    @PutMapping("/admin/user/profile")
    @AccessLimit(seconds = 60, maxCount = 10)
    @OperationLog(module = OperationModuleEnum.USER, type = OperationTypeEnum.UPDATE, description = "后台更新个人资料")
    public Result<Void> updateProfile(HttpServletRequest request, @RequestBody @Validated AdminUpdateProfileDTO dto) {
        String userId = (String) request.getAttribute("userId");
        userService.updateProfile(userId, dto);
        return Result.success();
    }

}
