package com.lsstop.mapper;

import com.lsstop.domain.entity.UserAuthEntity;
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
     * 插入用户认证信息
     *
     * @param userAuth 用户认证信息
     * @return 插入行数
     */
    int insertUserAuth(UserAuthEntity userAuth);

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
     * 根据用户ID查询邮箱认证信息
     *
     * @param userId 用户ID
     * @return 用户邮箱认证信息
     */
    UserAuthEntity selectEmailAuthByUserId(@Param("userId") String userId);

    /**
     * 根据用户ID更新密码
     *
     * @param userId     用户ID
     * @param credential 新密码（已加密）
     * @return 更新行数
     */
    int updateCredentialByUserId(@Param("userId") String userId, @Param("credential") String credential);

    /**
     * 根据用户ID和登录类型查询是否存在认证记录
     *
     * @param userId    用户ID
     * @param loginType 登录类型
     * @return 认证记录数
     */
    int countByUserIdAndType(@Param("userId") String userId, @Param("loginType") Integer loginType);

    /**
     * 根据用户ID和登录类型删除认证记录（软删除）
     *
     * @param userId    用户ID
     * @param loginType 登录类型
     * @return 删除行数
     */
    int deleteByUserIdAndType(@Param("userId") String userId, @Param("loginType") Integer loginType);

}
