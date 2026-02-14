package com.lsstop.mapper;

import com.lsstop.domain.entity.UserAuthEntity;
import com.lsstop.domain.entity.UserEntity;
import com.lsstop.domain.entity.UserProfileEntity;
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

}
