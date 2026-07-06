package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.common.Result;
import com.lsstop.domain.dto.EmailCodeLoginDTO;
import com.lsstop.domain.dto.EmailLoginDTO;
import com.lsstop.domain.dto.QQLoginDTO;
import com.lsstop.domain.dto.RefreshTokenDTO;
import com.lsstop.domain.dto.RegisterDTO;
import com.lsstop.domain.dto.ResetPasswordDTO;
import com.lsstop.domain.dto.SendCodeDTO;
import com.lsstop.domain.dto.WeiboLoginDTO;
import com.lsstop.domain.vo.LoginVO;
import com.lsstop.domain.vo.TokenVO;
import com.lsstop.enums.LoginSourceEnum;
import com.lsstop.service.AuthService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制层
 *
 * @author lishusheng
 * @date 2026/01/03
 */
@RestController
public class AuthController {

    @Resource
    private AuthService authService;

    /**
     * 邮箱密码登录
     *
     * @param dto 邮箱登录参数
     * @return 登录结果
     */
    @PostMapping("/front/auth/login/email")
    @AccessLimit(seconds = 60, maxCount = 10)
    public Result<LoginVO> emailLogin(@RequestBody @Validated EmailLoginDTO dto) {
        return Result.success(authService.emailLogin(dto, LoginSourceEnum.FRONT));
    }

    /**
     * 邮箱验证码登录
     *
     * @param dto 验证码登录参数
     * @return 登录结果
     */
    @PostMapping("/front/auth/login/email-code")
    @AccessLimit(seconds = 60, maxCount = 10)
    public Result<LoginVO> emailCodeLogin(@RequestBody @Validated EmailCodeLoginDTO dto) {
        return Result.success(authService.emailCodeLogin(dto, LoginSourceEnum.FRONT));
    }

    /**
     * QQ登录
     *
     * @param dto QQ登录参数
     * @return 登录结果
     */
    @PostMapping("/front/auth/login/qq")
    @AccessLimit(seconds = 60, maxCount = 10)
    public Result<LoginVO> qqLogin(@RequestBody @Validated QQLoginDTO dto) {
        return Result.success(authService.qqLogin(dto, LoginSourceEnum.FRONT));
    }

    /**
     * 微博登录
     *
     * @param dto 微博登录参数
     * @return 登录结果
     */
    @PostMapping("/front/auth/login/weibo")
    @AccessLimit(seconds = 60, maxCount = 10)
    public Result<LoginVO> weiboLogin(@RequestBody @Validated WeiboLoginDTO dto) {
        return Result.success(authService.weiboLogin(dto, LoginSourceEnum.FRONT));
    }

    /**
     * 刷新token
     *
     * @param dto 刷新token请求参数
     * @return 新的token信息
     */
    @PostMapping("/front/auth/refresh")
    @AccessLimit(seconds = 60, maxCount = 10)
    public Result<TokenVO> refreshToken(@RequestBody @Validated RefreshTokenDTO dto) {
        return Result.success(authService.refreshToken(dto.getRefreshToken(), LoginSourceEnum.FRONT));
    }

    /**
     * 退出登录
     *
     * @param dto 包含refreshToken的请求参数
     * @return 操作结果
     */
    @PostMapping("/front/auth/logout")
    @AccessLimit(seconds = 60, maxCount = 10)
    public Result<Void> logout(@RequestBody @Validated RefreshTokenDTO dto) {
        authService.logout(dto.getRefreshToken(), LoginSourceEnum.FRONT);
        return Result.success();
    }

    /**
     * 发送邮箱验证码
     *
     * @param dto 发送验证码请求参数
     * @return 操作结果
     */
    @PostMapping("/front/auth/code")
    @AccessLimit(seconds = 60, maxCount = 5)
    public Result<Void> sendCode(@RequestBody @Validated SendCodeDTO dto) {
        authService.sendCode(dto);
        return Result.success();
    }

    /**
     * 重置密码
     *
     * @param dto 重置密码请求参数
     * @return 操作结果
     */
    @PostMapping("/front/auth/reset-password")
    @AccessLimit(seconds = 60, maxCount = 5)
    public Result<Void> resetPassword(@RequestBody @Validated ResetPasswordDTO dto) {
        authService.resetPassword(dto);
        return Result.success();
    }

    /**
     * 用户注册
     *
     * @param dto 注册请求参数
     * @return 登录结果（注册成功后自动登录）
     */
    @PostMapping("/front/auth/register")
    @AccessLimit(seconds = 60, maxCount = 5)
    public Result<LoginVO> register(@RequestBody @Validated RegisterDTO dto) {
        return Result.success(authService.register(dto));
    }

    /**
     * 后台邮箱密码登录
     *
     * @param dto 邮箱登录参数
     * @return 登录结果
     */
    @PostMapping("/admin/auth/login/email")
    @AccessLimit(seconds = 60, maxCount = 10)
    public Result<LoginVO> adminEmailLogin(@RequestBody @Validated EmailLoginDTO dto) {
        return Result.success(authService.emailLogin(dto, LoginSourceEnum.ADMIN));
    }

    /**
     * 后台邮箱验证码登录
     *
     * @param dto 验证码登录参数
     * @return 登录结果
     */
    @PostMapping("/admin/auth/login/email-code")
    @AccessLimit(seconds = 60, maxCount = 10)
    public Result<LoginVO> adminEmailCodeLogin(@RequestBody @Validated EmailCodeLoginDTO dto) {
        return Result.success(authService.emailCodeLogin(dto, LoginSourceEnum.ADMIN));
    }

    /**
     * 后台刷新token
     *
     * @param dto 刷新token请求参数
     * @return 新的token信息
     */
    @PostMapping("/admin/auth/refresh")
    @AccessLimit(seconds = 60, maxCount = 10)
    public Result<TokenVO> adminRefreshToken(@RequestBody @Validated RefreshTokenDTO dto) {
        return Result.success(authService.refreshToken(dto.getRefreshToken(), LoginSourceEnum.ADMIN));
    }

    /**
     * 后台退出登录
     *
     * @param dto 包含refreshToken的请求参数
     * @return 操作结果
     */
    @PostMapping("/admin/auth/logout")
    @AccessLimit(seconds = 60, maxCount = 10)
    public Result<Void> adminLogout(@RequestBody @Validated RefreshTokenDTO dto) {
        authService.logout(dto.getRefreshToken(), LoginSourceEnum.ADMIN);
        return Result.success();
    }

    /**
     * 后台QQ登录
     *
     * @param dto QQ登录参数
     * @return 登录结果
     */
    @PostMapping("/admin/auth/login/qq")
    @AccessLimit(seconds = 60, maxCount = 10)
    public Result<LoginVO> adminQQLogin(@RequestBody @Validated QQLoginDTO dto) {
        return Result.success(authService.qqLogin(dto, LoginSourceEnum.ADMIN));
    }

    /**
     * 后台发送邮箱验证码
     *
     * @param dto 发送验证码请求参数
     * @return 操作结果
     */
    @PostMapping("/admin/auth/code")
    @AccessLimit(seconds = 60, maxCount = 5)
    public Result<Void> adminSendCode(@RequestBody @Validated SendCodeDTO dto) {
        authService.sendCode(dto);
        return Result.success();
    }

    /**
     * 后台微博登录
     *
     * @param dto 微博登录参数
     * @return 登录结果
     */
    @PostMapping("/admin/auth/login/weibo")
    @AccessLimit(seconds = 60, maxCount = 10)
    public Result<LoginVO> adminWeiboLogin(@RequestBody @Validated WeiboLoginDTO dto) {
        return Result.success(authService.weiboLogin(dto, LoginSourceEnum.ADMIN));
    }

}
