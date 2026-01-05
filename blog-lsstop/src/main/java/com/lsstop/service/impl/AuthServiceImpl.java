package com.lsstop.service.impl;

import com.lsstop.constant.RedisConst;
import com.lsstop.domain.dataObject.UserAuthDO;
import com.lsstop.domain.dataObject.UserProfileDO;
import com.lsstop.domain.dto.EmailLoginDTO;
import com.lsstop.domain.dto.QQLoginDTO;
import com.lsstop.domain.dto.WeiboLoginDTO;
import com.lsstop.domain.vo.LoginVO;
import com.lsstop.domain.vo.TokenVO;
import com.lsstop.enums.LoginTypeEnum;
import com.lsstop.exception.BusinessException;
import com.lsstop.mapper.AuthMapper;
import com.lsstop.service.AuthService;
import com.lsstop.utils.JwtUtils;
import com.lsstop.utils.PasswordUtils;
import com.lsstop.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现类
 *
 * @author lishusheng
 * @date 2026/01/03
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private AuthMapper authMapper;

    @Resource
    private JwtUtils jwtUtils;

    @Resource
    private RedisUtils redisUtils;

    /**
     * 邮箱密码登录
     *
     * @param dto 邮箱登录参数
     * @return 用户信息
     */
    @Override
    public LoginVO emailLogin(EmailLoginDTO dto) {
        // 查询用户认证信息
        UserAuthDO userAuth = authMapper.selectByIdentifierAndType(dto.getEmail(), LoginTypeEnum.EMAIL.getCode());
        if (userAuth == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证密码
        if (!PasswordUtils.verify(dto.getPassword(), userAuth.getCredential())) {
            throw new BusinessException("密码错误");
        }

        return buildLoginVO(userAuth);
    }

    /**
     * QQ登录
     *
     * @param dto QQ登录参数
     * @return 用户信息
     */
    @Override
    public LoginVO qqLogin(QQLoginDTO dto) {
        // TODO: 调用QQ开放平台接口验证access_token
        // 1. 使用accessToken调用QQ接口获取openId
        // 2. 根据 openId 查询用户认证信息
        // UserAuthDO userAuth = authMapper.selectByIdentifierAndType(openId, LoginTypeEnum.QQ.getCode());
        // 3. 如果用户不存在，可考虑自动注册
        // 4. 返回登录结果
        // return buildLoginVO(userAuth);
        throw new BusinessException("QQ登录功能暂未实现");
    }

    /**
     * 微博登录
     *
     * @param dto 微博登录参数
     * @return 用户信息
     */
    @Override
    public LoginVO weiboLogin(WeiboLoginDTO dto) {
        // TODO: 调用微博开放平台接口验证access_token
        // 1. 使用accessToken调用微博接口获取uid
        // 2. 根据 uid 查询用户认证信息
        // UserAuthDO userAuth = authMapper.selectByIdentifierAndType(uid, LoginTypeEnum.WEIBO.getCode());
        // 3. 如果用户不存在，可考虑自动注册
        // 4. 返回登录结果
        // return buildLoginVO(userAuth);
        throw new BusinessException("微博登录功能暂未实现");
    }

    /**
     * 构建登录返回结果
     *
     * @param userAuth  用户认证信息
     * @return 登录结果
     */
    private LoginVO buildLoginVO(UserAuthDO userAuth) {
        // 查询用户资料
        UserProfileDO userProfileDO = authMapper.selectProfileById(userAuth.getUserId());
        LoginVO loginVO = userProfileDO.asViewObject(LoginVO.class);

        // 生成token
        JwtUtils.TokenPair tokenPair = jwtUtils.generateFrontTokenPair(userAuth.getUserId());
        loginVO.setAccessToken(tokenPair.getAccessToken());
        loginVO.setRefreshToken(tokenPair.getRefreshToken());

        // 存储refreshToken到Redis
        redisUtils.set(RedisConst.FRONT_REFRESH_TOKEN + userAuth.getUserId(), tokenPair.getRefreshToken());

        return loginVO;
    }

    /**
     * 用户登出
     * <p>根据refreshToken解析用户ID，清除Redis中存储的refreshToken</p>
     * <p>宽松处理：即使token过期或无效，也不报错，确保用户能正常退出</p>
     *
     * @param refreshToken 刷新令牌
     */
    @Override
    public void logout(String refreshToken) {
        try {
            String userId = jwtUtils.getSubjectIgnoreExpiration(refreshToken);
            if (userId != null) {
                redisUtils.delete(RedisConst.FRONT_REFRESH_TOKEN + userId);
            }
        } catch (Exception e) {
            // 静默处理，退出登录不应因为token解析失败而报错
        }
    }

    /**
     * 刷新token
     * <p>验证refreshToken有效性，生成新的accessToken和refreshToken</p>
     *
     * @param refreshToken 刷新token
     * @return 新的token信息
     */
    @Override
    public TokenVO refreshToken(String refreshToken) {
        // 验证refreshToken
        if (!jwtUtils.validateRefreshToken(refreshToken)) {
            throw new BusinessException("refreshToken无效");
        }
        // 获取用户ID
        String userId = jwtUtils.getSubject(refreshToken);
        // 检查Redis中的refreshToken是否一致
        String storedToken = redisUtils.get(RedisConst.FRONT_REFRESH_TOKEN + userId, String.class);
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new BusinessException("refreshToken已过期");
        }
        // 生成新token
        JwtUtils.TokenPair tokenPair = jwtUtils.generateFrontTokenPair(userId);
        // 更新Redis中的refreshToken
        redisUtils.set(RedisConst.FRONT_REFRESH_TOKEN + userId, tokenPair.getRefreshToken());
        // 返回新token
        return TokenVO.builder()
                .accessToken(tokenPair.getAccessToken())
                .refreshToken(tokenPair.getRefreshToken())
                .build();
    }

}
