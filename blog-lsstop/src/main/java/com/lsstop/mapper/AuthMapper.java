package com.lsstop.mapper;

import com.lsstop.domain.dataObject.LoginDTO;
import com.lsstop.domain.dataObject.UserAuthDO;
import com.lsstop.domain.dataObject.UserProfileDO;
import org.apache.ibatis.annotations.Param;

/**
 * 认证数据访问层
 *
 * @author lishusheng
 * @date 2026/01/03
 */
public interface AuthMapper {

    /**
     * 根据登录信息查询用户认证信息
     *
     * @param loginDTO 登录参数（包含标识和登录方式）
     * @return 用户认证信息
     */
    UserAuthDO selectByLoginDTO(@Param("dto") LoginDTO loginDTO);

    /**
     * 根据用户ID查询用户资料
     *
     * @param userId 用户ID
     * @return 用户资料信息
     */
    UserProfileDO selectProfileById(@Param("userId") String userId);

}
