package com.lsstop.service.impl;

import com.lsstop.constant.RedisConst;
import com.lsstop.domain.vo.UserLikeVO;
import com.lsstop.enums.LikeTypeEnum;
import com.lsstop.service.LikeService;
import com.lsstop.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 点赞服务实现类
 *
 * @author lishusheng
 * @date 2026/01/02
 */
@Service
public class LikeServiceImpl implements LikeService {

    @Resource
    private RedisUtils redisUtils;

    /**
     * 点赞或取消点赞
     *
     * @param userId   用户id
     * @param targetId 目标id
     * @param type     点赞类型枚举
     * @return 操作后的点赞状态，true表示已点赞，false表示已取消
     */
    @Override
    public boolean toggleLike(String userId, Integer targetId, LikeTypeEnum type) {
        String likeCountKey = getLikeCountKey(type, targetId);
        String userLikeKey = getUserLikeKey(type, userId);
        String pendingSyncKey = getPendingSyncKey(type);
        String pendingField = userId + ":" + targetId;

        // 检查用户是否已点赞
        Boolean isLiked = redisUtils.sIsMember(userLikeKey, targetId);

        if (Boolean.TRUE.equals(isLiked)) {
            // 已点赞，取消点赞
            redisUtils.sRemove(userLikeKey, targetId);
            redisUtils.decrement(likeCountKey);
            // 记录待同步状态：0表示取消点赞
            redisUtils.hSet(pendingSyncKey, pendingField, 0);
            return false;
        } else {
            // 未点赞，添加点赞
            redisUtils.sAdd(userLikeKey, targetId);
            redisUtils.increment(likeCountKey);
            // 记录待同步状态：1表示点赞
            redisUtils.hSet(pendingSyncKey, pendingField, 1);
            return true;
        }
    }

    /**
     * 获取用户所有点赞信息
     *
     * @param userId 用户id
     * @return 用户点赞信息
     */
    @Override
    public UserLikeVO getUserLikes(String userId) {
        // 获取用户点赞的说说id
        Set<Integer> talkLikeIds = getLikeIds(RedisConst.USER_TALK_LIKE + userId);
        // 获取用户点赞的文章id
        Set<Integer> articleLikeIds = getLikeIds(RedisConst.USER_ARTICLE_LIKE + userId);
        // 获取用户点赞的评论id
        Set<Integer> commentLikeIds = getLikeIds(RedisConst.USER_COMMENT_LIKE + userId);

        return UserLikeVO.builder()
                .talkLikeIds(talkLikeIds)
                .articleLikeIds(articleLikeIds)
                .commentLikeIds(commentLikeIds)
                .build();
    }

    /**
     * 获取点赞数缓存key
     */
    private String getLikeCountKey(LikeTypeEnum type, Integer targetId) {
        return switch (type) {
            case TALK -> RedisConst.TALK_LIKE_COUNT + targetId;
            case ARTICLE -> RedisConst.ARTICLE_LIKE_COUNT + targetId;
            case COMMENT -> RedisConst.COMMENT_LIKE_COUNT + targetId;
        };
    }

    /**
     * 获取用户点赞记录缓存key
     */
    private String getUserLikeKey(LikeTypeEnum type, String userId) {
        return switch (type) {
            case TALK -> RedisConst.USER_TALK_LIKE + userId;
            case ARTICLE -> RedisConst.USER_ARTICLE_LIKE + userId;
            case COMMENT -> RedisConst.USER_COMMENT_LIKE + userId;
        };
    }

    /**
     * 获取待同步点赞记录缓存key
     */
    private String getPendingSyncKey(LikeTypeEnum type) {
        return switch (type) {
            case TALK -> RedisConst.LIKE_PENDING_SYNC + "talk";
            case ARTICLE -> RedisConst.LIKE_PENDING_SYNC + "article";
            case COMMENT -> RedisConst.LIKE_PENDING_SYNC + "comment";
        };
    }

    /**
     * 从Redis获取点赞id集合
     */
    private Set<Integer> getLikeIds(String key) {
        Set<Object> members = redisUtils.sMembers(key);
        if (members == null || members.isEmpty()) {
            return Collections.emptySet();
        }
        return members.stream()
                .map(obj -> (Integer) obj)
                .collect(Collectors.toSet());
    }

}
