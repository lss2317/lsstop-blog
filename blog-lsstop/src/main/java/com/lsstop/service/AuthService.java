package com.lsstop.service;

import com.lsstop.domain.dto.EmailLoginDTO;
import com.lsstop.domain.dto.QQLoginDTO;
import com.lsstop.domain.dto.WeiboLoginDTO;
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
     * 邮箱密码登录
     *
     * @param dto 邮箱登录参数
     * @return 用户信息
     */
    LoginVO emailLogin(EmailLoginDTO dto);

    /**
     * QQ登录
     *
     * @param dto QQ登录参数
     * @return 用户信息
     */
    LoginVO qqLogin(QQLoginDTO dto);

    /**
     * 微博登录
     *
     * @param dto 微博登录参数
     * @return 用户信息
     */
    LoginVO weiboLogin(WeiboLoginDTO dto);

    /**
     * 用户登出
     * <p>根据refreshToken解析用户ID，清除Redis中的令牌</p>
     *
     * @param refreshToken 刷新令牌
     */
    void logout(String refreshToken);

    /**
     * 刷新token
     *
     * @param refreshToken 刷新token
     * @return 新的token信息
     */
    TokenVO refreshToken(String refreshToken);

}
