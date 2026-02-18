package com.lsstop.service.impl;

import com.lsstop.constant.AuthConst;
import com.lsstop.constant.RedisConst;
import com.lsstop.domain.entity.UserProfileEntity;
import com.lsstop.domain.vo.UserInfoVO;
import com.lsstop.exception.BusinessException;
import com.lsstop.mapper.AuthMapper;
import com.lsstop.service.UserService;
import com.lsstop.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 *
 * @author lishusheng
 * @date 2026/01/03
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private AuthMapper authMapper;

    @Resource
    private RedisUtils redisUtils;

    /**
     * 根据用户ID获取用户资料
     *
     * @param userId 用户ID
     * @return 用户资料信息
     */
    @Override
    public UserInfoVO getUserProfile(String userId) {
        String cacheKey = RedisConst.USER_INFO + userId;
        // 先从缓存获取
        UserInfoVO cachedUser = redisUtils.get(cacheKey, UserInfoVO.class);
        if (cachedUser != null) {
            return cachedUser;
        }
        // 缓存不存在，查询数据库
        UserProfileEntity userProfile = authMapper.selectProfileById(userId);
        if (userProfile == null) {
            throw new BusinessException(AuthConst.USER_NOT_FOUND);
        }
        UserInfoVO userInfoVO = userProfile.asViewObject(UserInfoVO.class);
        // 写入缓存，过期时间1小时
        redisUtils.set(cacheKey, userInfoVO, RedisConst.EXPIRE_ONE_HOUR);
        return userInfoVO;
    }

}
