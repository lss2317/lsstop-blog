package com.lsstop.service.impl;

import com.lsstop.constant.CommonConst;
import com.lsstop.constant.RedisConst;
import com.lsstop.domain.vo.UserLikeVO;
import com.lsstop.enums.LikeTypeEnum;
import com.lsstop.service.LikeService;
import com.lsstop.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
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

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 点赞/取消点赞的原子脚本
     * <p>将"判断是否已点赞 → 增删点赞记录 → 增减点赞计数 → 累加今日计数 → 写待同步状态"
     * 收敛为单次原子执行，避免 check-then-act 竞态（如用户快速双击导致计数与状态不一致）。
     * <p>返回值：1 表示本次操作后为已点赞，0 表示已取消。
     * <p>说明：Set 成员、计数、Hash 值在现有序列化下均为纯数字/字符串，可直接用 StringRedisTemplate 操作。
     */
    private static final DefaultRedisScript<Long> TOGGLE_LIKE_SCRIPT;

    static {
        String lua = """
                local userLikeKey = KEYS[1]
                local likeCountKey = KEYS[2]
                local pendingSyncKey = KEYS[3]
                local todayKey = KEYS[4]
                local targetId = ARGV[1]
                local pendingField = ARGV[2]
                local todayExpire = tonumber(ARGV[3])
                if redis.call('SISMEMBER', userLikeKey, targetId) == 1 then
                    redis.call('SREM', userLikeKey, targetId)
                    redis.call('DECR', likeCountKey)
                    redis.call('HSET', pendingSyncKey, pendingField, 0)
                    return 0
                else
                    redis.call('SADD', userLikeKey, targetId)
                    redis.call('INCR', likeCountKey)
                    local todayVal = redis.call('INCR', todayKey)
                    if todayVal == 1 then
                        redis.call('EXPIRE', todayKey, todayExpire)
                    end
                    redis.call('HSET', pendingSyncKey, pendingField, 1)
                    return 1
                end
                """;
        TOGGLE_LIKE_SCRIPT = new DefaultRedisScript<>();
        TOGGLE_LIKE_SCRIPT.setScriptText(lua);
        TOGGLE_LIKE_SCRIPT.setResultType(Long.class);
    }

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
        // 前置校验，防止无效数据写入Redis
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException(CommonConst.USER_ID_REQUIRED);
        }
        if (targetId == null) {
            throw new IllegalArgumentException(CommonConst.TARGET_ID_REQUIRED);
        }

        String likeCountKey = getLikeCountKey(type, targetId);
        String userLikeKey = getUserLikeKey(type, userId);
        String pendingSyncKey = getPendingSyncKey(type);
        String pendingField = userId + ":" + targetId;
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String todayKey = RedisConst.TODAY_LIKE_COUNT + today;

        // 原子执行：整段点赞/取消逻辑在 Redis 单线程内一次完成，杜绝并发竞态
        Long result = stringRedisTemplate.execute(
                TOGGLE_LIKE_SCRIPT,
                List.of(userLikeKey, likeCountKey, pendingSyncKey, todayKey),
                String.valueOf(targetId),
                pendingField,
                String.valueOf(RedisConst.EXPIRE_ONE_DAY * 2)
        );
        // 点赞趋势缓存包含今日Redis实时计数，状态变化后立即失效
        redisUtils.delete(RedisConst.DASHBOARD_INTERACTION_TREND + today);
        return result != null && result == 1L;
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
                .map(obj -> ((Number) obj).intValue())
                .collect(Collectors.toSet());
    }

}
