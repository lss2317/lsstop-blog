package com.lsstop.service;

import com.lsstop.domain.dto.BindCodeDTO;
import com.lsstop.domain.dto.ChangeEmailDTO;
import com.lsstop.domain.dto.ChangePasswordDTO;
import com.lsstop.domain.dto.EmailCodeLoginDTO;
import com.lsstop.domain.dto.EmailLoginDTO;
import com.lsstop.domain.dto.QQLoginDTO;
import com.lsstop.domain.dto.RegisterDTO;
import com.lsstop.domain.dto.ResetPasswordDTO;
import com.lsstop.domain.dto.SendCodeDTO;
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
     * 邮箱验证码登录
     *
     * @param dto 验证码登录参数
     * @return 用户信息
     */
    LoginVO emailCodeLogin(EmailCodeLoginDTO dto);

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

    /**
     * 发送邮箱验证码
     *
     * @param dto 发送验证码请求参数
     */
    void sendCode(SendCodeDTO dto);

    /**
     * 重置密码
     *
     * @param dto 重置密码请求参数
     */
    void resetPassword(ResetPasswordDTO dto);

    /**
     * 用户注册
     *
     * @param dto 注册请求参数
     * @return 登录结果（注册成功后自动登录）
     */
    LoginVO register(RegisterDTO dto);

    /**
     * 修改绑定邮箱
     *
     * @param userId 用户ID
     * @param dto    修改邮箱请求参数
     */
    void changeEmail(String userId, ChangeEmailDTO dto);

    /**
     * 修改密码
     *
     * @param userId 用户ID
     * @param dto    修改密码请求参数
     */
    void changePassword(String userId, ChangePasswordDTO dto);

    /**
     * 绑定QQ
     *
     * @param userId 用户ID
     * @param dto    绑定请求参数
     */
    void bindQQ(String userId, BindCodeDTO dto);

    /**
     * 绑定微博
     *
     * @param userId 用户ID
     * @param dto    绑定请求参数
     */
    void bindWeibo(String userId, BindCodeDTO dto);

}
