package com.lsstop.service;

import com.lsstop.domain.vo.UserProfileVO;
import com.lsstop.domain.vo.UserPublicProfileVO;
import com.lsstop.domain.vo.UserInfoVO;
import org.springframework.web.multipart.MultipartFile;

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
     * 获取用户公开主页详情（查看他人）
     *
     * @param userId 用户ID
     * @return 用户公开主页详情
     */
    UserPublicProfileVO getUserHomeDetail(String userId);

    /**
     * 获取当前用户主页详情（查看自己）
     *
     * @param userId 用户ID
     * @return 用户完整主页详情
     */
    UserProfileVO getMyHomeDetail(String userId);

    /**
     * 更新用户头像
     *
     * @param userId 用户ID
     * @param file   头像文件
     * @return 新头像URL
     */
    String updateAvatar(String userId, MultipartFile file);

}
