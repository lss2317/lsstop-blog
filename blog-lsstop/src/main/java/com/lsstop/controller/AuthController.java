package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.common.Result;
import com.lsstop.domain.dataObject.LoginDTO;
import com.lsstop.domain.dto.RefreshTokenDTO;
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
     * 登录
     *
     * @param loginDTO 登录参数
     * @return 登录结果
     */
    @PostMapping("/front/auth/login")
    @AccessLimit(seconds = 60, maxCount = 10)
    public Result<LoginVO> login(@RequestBody @Validated LoginDTO loginDTO) {
        return Result.success(authService.login(loginDTO));
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
