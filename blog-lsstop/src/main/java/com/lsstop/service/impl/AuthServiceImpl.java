package com.lsstop.service.impl;

import com.lsstop.constant.RedisConst;
import com.lsstop.domain.dataObject.LoginDTO;
import com.lsstop.domain.dataObject.UserAuthDO;
import com.lsstop.domain.dataObject.UserProfileDO;
import com.lsstop.domain.vo.LoginVO;
import com.lsstop.enums.LoginTypeEnum;
import com.lsstop.mapper.AuthMapper;
import com.lsstop.service.AuthService;
import com.lsstop.utils.JwtUtils;
import com.lsstop.utils.PasswordUtils;
import com.lsstop.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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
     * 用户登录
     * <ul>
     *     <li>邮箱密码登录：验证密码</li>
     *     <li>QQ登录：验证QQ access_token</li>
     *     <li>微博登录：验证微博access_token</li>
     * </ul>
     *
     * @param loginDTO 登录参数
     * @return 用户信息
     */
    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 根据标识和登录方式查询用户认证信息
        UserAuthDO userAuth = authMapper.selectByLoginDTO(loginDTO);
        if (userAuth == null) {
            throw new RuntimeException("用户不存在");
        }

        // 根据登录方式进行不同的验证
        LoginTypeEnum loginType = LoginTypeEnum.getByCode(loginDTO.getLoginType());
        if (loginType == null) {
            throw new RuntimeException("不支持的登录方式");
        }

        switch (loginType) {
            case EMAIL -> validateEmailLogin(userAuth, loginDTO);
            case QQ -> validateQQLogin(userAuth, loginDTO);
            case WEIBO -> validateWeiboLogin(userAuth, loginDTO);
        }

        // 查询用户资料
        UserProfileDO userProfileDO = authMapper.selectProfileById(userAuth.getUserId());
        LoginVO loginVO = userProfileDO.asViewObject(LoginVO.class);
        loginVO.setLoginType(loginDTO.getLoginType());

        // 生成token
        JwtUtils.TokenPair tokenPair = jwtUtils.generateFrontTokenPair(userAuth.getUserId());
        loginVO.setAccessToken(tokenPair.getAccessToken());
        loginVO.setRefreshToken(tokenPair.getRefreshToken());
        // 存储refreshToken到Redis
        redisUtils.set(RedisConst.FRONT_REFRESH_TOKEN + userAuth.getUserId(), tokenPair.getRefreshToken());
        return loginVO;
    }

    /**
     * 邮箱密码登录验证
     * <p>使用PBKDF2WithHmacSHA256算法验证密码，将用户输入的明文密码与数据库中存储的加密密码进行比对</p>
     *
     * @param userAuth 用户认证信息（credential字段存储加密后的密码）
     * @param loginDTO 登录参数（credential字段为用户输入的明文密码）
     */
    private void validateEmailLogin(UserAuthDO userAuth, LoginDTO loginDTO) {
        if (!PasswordUtils.verify(loginDTO.getCredential(), userAuth.getCredential())) {
            throw new RuntimeException("密码错误");
        }
    }

    /**
     * QQ登录验证
     * <p>验证QQ OAuth返回的access_token有效性</p>
     *
     * @param userAuth 用户认证信息
     * @param loginDTO 登录参数（credential为QQ access_token）
     */
    private void validateQQLogin(UserAuthDO userAuth, LoginDTO loginDTO) {
        // TODO: 调用QQ开放平台接口验证access_token
        // 1. 使用access_token调用QQ接口获取openId
        // 2. 比对openId与userAuth.identifier是否一致
        throw new RuntimeException("QQ登录功能暂未实现");
    }

    /**
     * 微博登录验证
     * <p>验证微博OAuth返回的access_token有效性</p>
     *
     * @param userAuth 用户认证信息
     * @param loginDTO 登录参数（credential为微博access_token）
     */
    private void validateWeiboLogin(UserAuthDO userAuth, LoginDTO loginDTO) {
        // TODO: 调用微博开放平台接口验证access_token
        // 1. 使用access_token调用微博接口获取uid
        // 2. 比对uid与userAuth.identifier是否一致
        throw new RuntimeException("微博登录功能暂未实现");
    }

    /**
     * 用户登出
     * <p>清除Redis中存储的refreshToken</p>
     *
     * @param userId 用户ID
     */
    @Override
    public void logout(String userId) {
        redisUtils.delete(RedisConst.FRONT_REFRESH_TOKEN + userId);
    }

    /**
     * 刷新token
     * <p>验证refreshToken有效性，生成新的accessToken和refreshToken</p>
     *
     * @param refreshToken 刷新token
     * @return 新的token信息
     */
    @Override
    public Map<String, String> refreshToken(String refreshToken) {
        // 验证refreshToken
        if (!jwtUtils.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("refreshToken无效");
        }
        // 获取用户ID
        String userId = jwtUtils.getSubject(refreshToken);
        // 检查Redis中的refreshToken是否一致
        String storedToken = redisUtils.get(RedisConst.FRONT_REFRESH_TOKEN + userId, String.class);
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new RuntimeException("refreshToken已过期");
        }
        // 生成新token
        JwtUtils.TokenPair tokenPair = jwtUtils.generateFrontTokenPair(userId);
        // 更新Redis中的refreshToken
        redisUtils.set(RedisConst.FRONT_REFRESH_TOKEN + userId, tokenPair.getRefreshToken());
        // 返回新token
        Map<String, String> tokenMap = new HashMap<>(2);
        tokenMap.put("accessToken", tokenPair.getAccessToken());
        tokenMap.put("refreshToken", tokenPair.getRefreshToken());
        return tokenMap;
    }

}
