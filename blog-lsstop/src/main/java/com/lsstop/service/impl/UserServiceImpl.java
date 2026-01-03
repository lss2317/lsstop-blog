package com.lsstop.service.impl;

import com.lsstop.domain.dataObject.UserProfileDO;
import com.lsstop.domain.vo.UserInfoVO;
import com.lsstop.mapper.AuthMapper;
import com.lsstop.service.UserService;
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

    /**
     * 根据用户ID获取用户资料
     *
     * @param userId 用户ID
     * @return 用户资料信息
     */
    @Override
    public UserInfoVO getUserProfile(String userId) {
        UserProfileDO userProfile = authMapper.selectProfileById(userId);
        if (userProfile == null) {
            throw new RuntimeException("用户不存在");
        }
        return userProfile.asViewObject(UserInfoVO.class);
    }

}
