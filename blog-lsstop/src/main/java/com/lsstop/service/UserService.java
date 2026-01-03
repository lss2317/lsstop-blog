package com.lsstop.service;

import com.lsstop.domain.dataObject.UserProfileDO;
import com.lsstop.domain.vo.LoginVO;
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

}
