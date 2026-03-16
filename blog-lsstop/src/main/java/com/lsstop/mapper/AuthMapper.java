package com.lsstop.mapper;

import com.lsstop.domain.entity.UserAuthEntity;
import com.lsstop.domain.entity.UserEntity;
import com.lsstop.domain.entity.UserProfileEntity;
import com.lsstop.domain.vo.UserProfileVO;
import com.lsstop.domain.vo.UserPublicProfileVO;
import org.apache.ibatis.annotations.Param;

/**
 * 认证数据访问层
 *
 * @author lishusheng
 * @date 2026/01/03
 */
public interface AuthMapper {

    /**
     * 根据标识和登录类型查询用户认证信息
     * <p>标识根据登录类型不同：邮箱登录为email，QQ登录为openId，微博登录为uid</p>
     *
     * @param identifier 登录标识（邮箱/openId/uid）
     * @param loginType  登录类型
     * @return 用户认证信息
     */
    UserAuthEntity selectByIdentifierAndType(@Param("identifier") String identifier, @Param("loginType") Integer loginType);

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
     * 根据用户ID查询用户邮箱（邮箱登录方式）
     *
     * @param userId 用户ID
     * @return 用户邮箱，不存在返回null
     */
    String selectEmailByUserId(@Param("userId") String userId);

    /**
     * 更新用户密码
     *
     * @param identifier 登录标识（邮箱）
     * @param loginType  登录类型
     * @param credential 新密码（已加密）
     * @return 更新行数
     */
    int updateCredential(@Param("identifier") String identifier, @Param("loginType") Integer loginType, @Param("credential") String credential);

    /**
     * 插入用户基础信息
     *
     * @param user 用户基础信息
     * @return 插入行数
     */
    int insertUser(UserEntity user);

    /**
     * 插入用户认证信息
     *
     * @param userAuth 用户认证信息
     * @return 插入行数
     */
    int insertUserAuth(UserAuthEntity userAuth);

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
     * 更新用户登录标识（如邮箱）
     *
     * @param userId        用户ID
     * @param loginType     登录类型
     * @param newIdentifier 新登录标识
     * @return 更新行数
     */
    int updateIdentifier(@Param("userId") String userId, @Param("loginType") Integer loginType, @Param("newIdentifier") String newIdentifier);

    /**
     * 更新用户头像
     *
     * @param userId 用户ID
     * @param avatar 头像URL
     * @return 更新行数
     */
    int updateAvatar(@Param("userId") String userId, @Param("avatar") String avatar);

}
