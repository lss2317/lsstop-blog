package com.lsstop.mapper;

import com.lsstop.domain.entity.UserEntity;
import com.lsstop.domain.entity.UserProfileEntity;
import com.lsstop.domain.vo.RoleBrief;
import com.lsstop.domain.vo.UserManageRoleVO;
import com.lsstop.domain.vo.UserManageVO;
import com.lsstop.domain.vo.UserProfileVO;
import com.lsstop.domain.vo.UserPublicProfileVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户数据访问层
 *
 * @author lishusheng
 * @date 2026/06/16
 */
public interface UserMapper {

    /**
     * 根据用户ID查询用户资料
     *
     * @param userId 用户ID
     * @return 用户资料信息
     */
    UserProfileEntity selectProfileById(@Param("userId") String userId);

    /**
     * 根据用户ID查询用户基础信息
     *
     * @param userId 用户ID
     * @return 用户基础信息
     */
    UserEntity selectUserById(@Param("userId") String userId);

    /**
     * 更新用户最后登录时间
     *
     * @param userId 用户ID
     */
    void updateLastLoginTime(@Param("userId") String userId);

    /**
     * 插入用户基础信息
     *
     * @param user 用户基础信息
     * @return 插入行数
     */
    int insertUser(UserEntity user);

    /**
     * 插入用户资料信息
     *
     * @param userProfile 用户资料信息
     * @return 插入行数
     */
    int insertUserProfile(UserProfileEntity userProfile);

    /**
     * 查询用户主页详情（包含基本信息、绑定状态、注册时间）
     *
     * @param userId 用户ID
     * @return 用户主页详情
     */
    UserProfileVO selectUserHomeDetail(@Param("userId") String userId);

    /**
     * 查询用户公开主页详情（不包含敏感信息）
     *
     * @param userId 用户ID
     * @return 用户公开主页详情
     */
    UserPublicProfileVO selectUserPublicHomeDetail(@Param("userId") String userId);

    /**
     * 更新用户头像
     *
     * @param userId 用户ID
     * @param avatar 头像URL
     * @return 更新行数
     */
    int updateAvatar(@Param("userId") String userId, @Param("avatar") String avatar);

    /**
     * 更新用户资料信息
     *
     * @param userId   用户ID
     * @param nickname 昵称
     * @param website  个人网站
     * @param intro    个人简介
     * @return 更新行数
     */
    int updateUserInfo(@Param("userId") String userId, @Param("nickname") String nickname,
                       @Param("website") String website, @Param("intro") String intro);

    /**
     * 更新用户资料信息（含头像）
     *
     * @param userId   用户ID
     * @param nickname 昵称
     * @param avatar   头像URL
     * @param website  个人网站
     * @param intro    个人简介
     * @return 更新行数
     */
    int updateUserProfile(@Param("userId") String userId, @Param("nickname") String nickname,
                           @Param("avatar") String avatar, @Param("website") String website,
                           @Param("intro") String intro);

    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param status 状态（0-禁用 1-正常）
     * @return 更新行数
     */
    int updateUserStatus(@Param("userId") String userId, @Param("status") Integer status);

    /**
     * 分页查询用户管理列表
     *
     * @param offset   偏移量
     * @param size     每页数量
     * @param userId   用户ID（精确匹配）
     * @param nickname 昵称（模糊搜索）
     * @param email    邮箱（模糊搜索）
     * @param status   状态（0-禁用 1-正常）
     * @return 用户列表
     */
    List<UserManageVO> selectUserList(@Param("offset") Integer offset,
                                      @Param("size") Integer size,
                                      @Param("userId") String userId,
                                      @Param("nickname") String nickname,
                                      @Param("email") String email,
                                      @Param("status") Integer status);

    /**
     * 统计用户总数
     *
     * @param userId   用户ID（精确匹配）
     * @param nickname 昵称（模糊搜索）
     * @param email    邮箱（模糊搜索）
     * @param status   状态（0-禁用 1-正常）
     * @return 用户总数
     */
    Integer countUserTotal(@Param("userId") String userId,
                           @Param("nickname") String nickname,
                           @Param("email") String email,
                           @Param("status") Integer status);

    /**
     * 批量查询指定用户列表的角色信息
     *
     * @param userIds 用户ID列表
     * @return 用户角色列表（包含 userId 用于分组）
     */
    List<UserManageRoleVO> selectRolesByUserIds(@Param("userIds") List<String> userIds);

    /**
     * 批量插入用户角色关联（已存在活跃记录时幂等更新）
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     */
    void batchInsertUserRole(@Param("userId") String userId, @Param("roleIds") List<Integer> roleIds);

    /**
     * 查询用户当前有效角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Integer> selectRoleIdsByUserId(@Param("userId") String userId);

    /**
     * 查询单个用户的角色简要信息
     *
     * @param userId 用户ID
     * @return 角色简要信息列表
     */
    List<RoleBrief> selectRolesByUserId(@Param("userId") String userId);

    /**
     * 批量软删除用户角色关联
     *
     * @param userId    用户ID
     * @param roleIds   角色ID列表
     * @param deletedAt 删除时间戳
     */
    void batchSoftDeleteUserRole(@Param("userId") String userId,
                                  @Param("roleIds") List<Integer> roleIds,
                                  @Param("deletedAt") Long deletedAt);

    /**
     * 软删除用户基础信息
     *
     * @param userId    用户ID
     * @param deletedAt 删除时间戳
     */
    int deleteUserByUserId(@Param("userId") String userId, @Param("deletedAt") Long deletedAt);

    /**
     * 软删除用户资料
     *
     * @param userId    用户ID
     * @param deletedAt 删除时间戳
     */
    int deleteUserProfileByUserId(@Param("userId") String userId, @Param("deletedAt") Long deletedAt);
}
