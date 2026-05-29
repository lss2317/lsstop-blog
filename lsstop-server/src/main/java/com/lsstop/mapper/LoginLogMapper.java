package com.lsstop.mapper;

import com.lsstop.domain.entity.LoginLogEntity;
import com.lsstop.domain.vo.LoginLogVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 登录日志数据访问层
 *
 * @author lishusheng
 * @date 2026/01/17
 */
public interface LoginLogMapper {

    /**
     * 插入登录日志
     *
     * @param loginLog 登录日志实体
     */
    void insert(LoginLogEntity loginLog);

    /**
     * 查询用户最近一次成功登录的IP归属地
     *
     * @param userId 用户ID
     * @return IP归属地
     */
    String selectLatestIpRegionByUserId(@Param("userId") String userId);

    /**
     * 分页查询认证日志列表
     *
     * @param offset     偏移量
     * @param size       每页数量
     * @param userId     用户ID
     * @param actionType 操作类型：1-登录 2-退出 3-注册
     * @param state      操作结果：0-成功 1-失败
     * @param type       操作来源：0-前台 1-后台 2-非法
     * @param loginType  登录方式：1-邮箱 2-QQ 3-微博
     * @param noUserId   仅筛无用户ID的记录
     * @return 认证日志列表
     */
    List<LoginLogVO> selectList(@Param("offset") Integer offset,
                                @Param("size") Integer size,
                                @Param("userId") String userId,
                                @Param("actionType") Integer actionType,
                                @Param("state") Integer state,
                                @Param("type") Integer type,
                                @Param("loginType") Integer loginType,
                                @Param("noUserId") Boolean noUserId);

    /**
     * 统计认证日志总数
     *
     * @param userId     用户ID
     * @param actionType 操作类型：1-登录 2-退出 3-注册
     * @param state      操作结果：0-成功 1-失败
     * @param type       操作来源：0-前台 1-后台 2-非法
     * @param loginType  登录方式：1-邮箱 2-QQ 3-微博
     * @param noUserId   仅筛无用户ID的记录
     * @return 认证日志总数
     */
    Integer countTotal(@Param("userId") String userId,
                       @Param("actionType") Integer actionType,
                       @Param("state") Integer state,
                       @Param("type") Integer type,
                       @Param("loginType") Integer loginType,
                       @Param("noUserId") Boolean noUserId);

    /**
     * 软删除认证日志（支持单个和批量删除）
     *
     * @param logNumbers 日志编号列表
     * @param deletedAt  删除时间戳
     */
    void deleteByLogNumbers(@Param("logNumbers") List<String> logNumbers, @Param("deletedAt") Long deletedAt);

    /**
     * 查询所有认证日志（用于导出，不带分页）
     *
     * @param userId     用户ID
     * @param actionType 操作类型：1-登录 2-退出 3-注册
     * @param state      操作结果：0-成功 1-失败
     * @param type       操作来源：0-前台 1-后台 2-非法
     * @param loginType  登录方式：1-邮箱 2-QQ 3-微博
     * @param noUserId   仅筛无用户ID的记录
     * @return 认证日志列表
     */
    List<LoginLogVO> selectAllForExport(@Param("userId") String userId,
                                        @Param("actionType") Integer actionType,
                                        @Param("state") Integer state,
                                        @Param("type") Integer type,
                                        @Param("loginType") Integer loginType,
                                        @Param("noUserId") Boolean noUserId);

}
