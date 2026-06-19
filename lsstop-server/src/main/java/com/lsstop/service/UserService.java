package com.lsstop.service;

import com.lsstop.domain.dto.UpdateUserApiPermissionDTO;
import com.lsstop.domain.dto.UpdateUserInfoDTO;
import com.lsstop.domain.dto.UpdateUserMenuDTO;
import com.lsstop.domain.vo.AdminUserInfoVO;
import com.lsstop.domain.vo.UserManageVO;
import com.lsstop.domain.vo.UserProfileVO;
import com.lsstop.domain.vo.UserPublicProfileVO;
import com.lsstop.domain.vo.UserInfoVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    /**
     * 更新用户信息
     *
     * @param userId 用户ID
     * @param dto    更新用户信息参数
     */
    void updateUserInfo(String userId, UpdateUserInfoDTO dto);

    /**
     * 修改用户菜单权限（差量更新）
     * <p>接收全量菜单ID列表，后端与现有权限做差集计算后更新
     *
     * @param dto 修改用户菜单权限参数
     */
    void updateUserMenuPermission(UpdateUserMenuDTO dto);

    /**
     * 修改用户接口权限（差量更新）
     * <p>接收全量接口权限ID列表，后端与角色接口权限做差集计算后更新
     *
     * @param dto 修改用户接口权限参数
     */
    void updateUserApiPermission(UpdateUserApiPermissionDTO dto);

    /**
     * 获取后台当前登录用户信息
     *
     * @param userId 用户ID
     * @return 后台用户信息
     */
    AdminUserInfoVO getAdminUserInfo(String userId);

    /**
     * 分页查询用户管理列表
     *
     * @param current  当前页码
     * @param size     每页数量
     * @param userId   用户ID（精确匹配）
     * @param nickname 昵称（模糊搜索）
     * @param email    邮箱（模糊搜索）
     * @param status   状态（0-禁用 1-正常）
     * @return 用户列表
     */
    List<UserManageVO> listUsers(Integer current, Integer size, String userId, String nickname, String email, Integer status);

    /**
     * 统计用户总数
     *
     * @param userId   用户ID（精确匹配）
     * @param nickname 昵称（模糊搜索）
     * @param email    邮箱（模糊搜索）
     * @param status   状态（0-禁用 1-正常）
     * @return 用户总数
     */
    Integer countUserTotal(String userId, String nickname, String email, Integer status);

}
