package com.lsstop.service;

import com.lsstop.domain.vo.UserProfileVO;
import com.lsstop.domain.vo.UserInfoVO;

/**
 * 用户服务接口
 *
 * @author lishusheng
 * @date 2026/01/03
 */
public interface UserService {

    /**
     * 根据用户ID获取用户资料
     *
     * @param userId 用户ID
     * @return 用户资料信息
     */
    UserInfoVO getUserProfile(String userId);

    /**
     * 获取用户主页详情
     *
     * @param userId 用户ID
     * @return 用户主页详情
     */
    UserProfileVO getUserHomeDetail(String userId);

}
