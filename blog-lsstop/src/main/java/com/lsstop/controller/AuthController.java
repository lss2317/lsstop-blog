package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.common.Result;
import com.lsstop.domain.dto.EmailLoginDTO;
import com.lsstop.domain.dto.QQLoginDTO;
import com.lsstop.domain.dto.RefreshTokenDTO;
import com.lsstop.domain.dto.WeiboLoginDTO;
import com.lsstop.domain.vo.LoginVO;
import com.lsstop.domain.vo.TokenVO;
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
        return Result.success(authService.emailLogin(dto));
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
        return Result.success(authService.qqLogin(dto));
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
        return Result.success(authService.weiboLogin(dto));
    }

    /**
     * 刷新token
     *
     * @param dto 刷新token请求参数
     * @return 新的token信息
     */
    @PostMapping("/front/auth/refresh")
    public Result<TokenVO> refreshToken(@RequestBody @Validated RefreshTokenDTO dto) {
        return Result.success(authService.refreshToken(dto.getRefreshToken()));
    }

}
