package com.lsstop.service;

import com.lsstop.domain.dataObject.LoginDTO;
import com.lsstop.domain.vo.LoginVO;
import com.lsstop.domain.vo.TokenVO;

/**
 * 认证服务
 *
 * @author lishusheng
 * @date 2026/01/03
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param loginDTO 登录参数
     * @return 用户信息
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 用户登出
     *
     * @param userId 用户ID
     */
    void logout(String userId);

    /**
     * 刷新token
     *
     * @param refreshToken 刷新token
     * @return 新的token信息
     */
    TokenVO refreshToken(String refreshToken);

}
