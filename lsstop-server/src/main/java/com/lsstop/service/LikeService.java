package com.lsstop.service;

import com.lsstop.domain.vo.UserLikeVO;
import com.lsstop.enums.LikeTypeEnum;

/**
 * 点赞服务接口
 *
 * @author lishusheng
 * @date 2026/01/02
 */
public interface LikeService {

    /**
     * 点赞或取消点赞
     *
     * @param userId   用户id
     * @param targetId 目标id
     * @param type     点赞类型枚举
     * @return 操作后的点赞状态，true表示已点赞，false表示已取消
     */
    boolean toggleLike(String userId, Integer targetId, LikeTypeEnum type);

    /**
     * 获取用户所有点赞信息
     *
     * @param userId 用户id
     * @return 用户点赞信息
     */
    UserLikeVO getUserLikes(String userId);

}
