package com.lsstop.task;

import com.lsstop.constant.RedisConst;
import com.lsstop.domain.entity.LikeRecordEntity;
import com.lsstop.domain.vo.LikeCountVO;
import com.lsstop.enums.LikeTypeEnum;
import com.lsstop.mapper.LikeMapper;
import com.lsstop.utils.RedisUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 点赞统计任务
 * <p>项目启动时初始化点赞数据到Redis，定时同步点赞记录到数据库</p>
 *
 * @author lishusheng
 * @date 2026/01/26
 */
@Component
public class LikeStatsTask {

    private static final Logger log = LoggerFactory.getLogger(LikeStatsTask.class);

    @Resource
    private LikeMapper likeMapper;

    @Resource
    private RedisUtils redisUtils;

    /**
     * 项目启动时初始化点赞数据到Redis
     */
    @PostConstruct
    public void init() {
        clearOldData();
        initLikeCounts();
        initUserLikes();
    }

    /**
     * 清理Redis中的旧点赞数据
     */
    private void clearOldData() {
        log.info("开始清理Redis中的旧点赞数据...");
        // 清理点赞数
        redisUtils.deleteByPrefix(RedisConst.TALK_LIKE_COUNT);
        redisUtils.deleteByPrefix(RedisConst.ARTICLE_LIKE_COUNT);
        redisUtils.deleteByPrefix(RedisConst.COMMENT_LIKE_COUNT);
        // 清理用户点赞记录
        redisUtils.deleteByPrefix(RedisConst.USER_TALK_LIKE);
        redisUtils.deleteByPrefix(RedisConst.USER_ARTICLE_LIKE);
        redisUtils.deleteByPrefix(RedisConst.USER_COMMENT_LIKE);
        log.info("旧点赞数据清理完成");
    }

    /**
     * 初始化所有类型的点赞数
     */
    private void initLikeCounts() {
        log.info("开始初始化点赞数到Redis...");
        int count = 0;

        // 说说点赞数
        count += initLikeCountByType(LikeTypeEnum.TALK.getType(), RedisConst.TALK_LIKE_COUNT);
        // 文章点赞数
        count += initLikeCountByType(LikeTypeEnum.ARTICLE.getType(), RedisConst.ARTICLE_LIKE_COUNT);
        // 评论点赞数
        count += initLikeCountByType(LikeTypeEnum.COMMENT.getType(), RedisConst.COMMENT_LIKE_COUNT);

        log.info("点赞数初始化完成，共{}条", count);
    }

    /**
     * 按类型初始化点赞数
     */
    private int initLikeCountByType(Integer type, String keyPrefix) {
        List<LikeCountVO> likeCounts = likeMapper.countLikesByType(type);
        if (likeCounts == null || likeCounts.isEmpty()) {
            return 0;
        }
        for (LikeCountVO likeCount : likeCounts) {
            redisUtils.set(keyPrefix + likeCount.getTargetId(), likeCount.getLikeCount());
        }
        return likeCounts.size();
    }

    /**
     * 初始化用户点赞记录
     */
    private void initUserLikes() {
        log.info("开始初始化用户点赞记录到Redis...");
        List<LikeRecordEntity> likeRecords = likeMapper.listValidLikes();
        if (likeRecords == null || likeRecords.isEmpty()) {
            log.info("没有点赞记录需要初始化");
            return;
        }
        int count = 0;
        for (LikeRecordEntity record : likeRecords) {
            String userLikeKey = getUserLikeKey(record.getType(), record.getUserId());
            if (userLikeKey != null) {
                redisUtils.sAdd(userLikeKey, record.getTargetId());
                count++;
            }
        }
        log.info("用户点赞记录初始化完成，共{}条", count);
    }

    /**
     * 根据点赞类型获取用户点赞记录缓存key
     */
    private String getUserLikeKey(Integer type, String userId) {
        LikeTypeEnum likeType = LikeTypeEnum.of(type);
        if (likeType == null) {
            return null;
        }
        return switch (likeType) {
            case TALK -> RedisConst.USER_TALK_LIKE + userId;
            case ARTICLE -> RedisConst.USER_ARTICLE_LIKE + userId;
            case COMMENT -> RedisConst.USER_COMMENT_LIKE + userId;
        };
    }

    /**
     * 定时同步点赞记录到数据库
     * 每5分钟执行一次，首次延迟5分钟执行避免与初始化冲突
     */
    @Scheduled(fixedDelay = 5 * 60 * 1000, initialDelay = 5 * 60 * 1000)
    public void syncLikeRecordsToDb() {
        log.info("开始同步点赞记录到数据库...");
        int totalCount = 0;

        // 同步三种类型的点赞记录，单个类型失败不影响其他类型
        for (LikeTypeEnum likeType : LikeTypeEnum.values()) {
            try {
                totalCount += syncLikesByType(likeType);
            } catch (Exception e) {
                log.error("同步{}点赞记录失败", likeType.getDesc(), e);
            }
        }

        log.info("点赞记录同步完成，共处理{}条", totalCount);
    }

    /**
     * 按类型同步点赞记录
     */
    private int syncLikesByType(LikeTypeEnum likeType) {
        String pendingKey = RedisConst.LIKE_PENDING_SYNC + likeType.name().toLowerCase();

        // 获取所有待同步的点赞记录
        Map<Object, Object> pendingRecords = redisUtils.hGetAll(pendingKey);
        if (CollectionUtils.isEmpty(pendingRecords)) {
            return 0;
        }

        List<LikeRecordEntity> records = new ArrayList<>();
        List<Object> processedFields = new ArrayList<>();

        for (Map.Entry<Object, Object> entry : pendingRecords.entrySet()) {
            String field = (String) entry.getKey();
            Object statusObj = entry.getValue();
            int status = statusObj instanceof Number ? ((Number) statusObj).intValue() : 0;

            // 解析 field: userId:targetId
            String[] parts = field.split(":");
            if (parts.length != 2) {
                processedFields.add(field);
                continue;
            }

            String userId = parts[0];
            // 过滤空字符串的userId，避免脏数据写入数据库
            if (userId == null || userId.trim().isEmpty()) {
                log.warn("发现无效的点赞记录，userId为空，field: {}", field);
                processedFields.add(field);
                continue;
            }

            int targetId;
            try {
                targetId = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                log.warn("发现无效的点赞记录，targetId解析失败，field: {}", field);
                processedFields.add(field);
                continue;
            }

            LikeRecordEntity record = LikeRecordEntity.builder()
                    .userId(userId)
                    .targetId(targetId)
                    .type(likeType.getType())
                    .status(status)
                    .build();
            records.add(record);
            processedFields.add(field);
        }

        if (!records.isEmpty()) {
            // 批量插入或更新
            likeMapper.batchInsertOrUpdate(records);
            log.info("同步{}点赞记录{}条", likeType.getDesc(), records.size());
        }

        // 删除所有已处理的记录（包括无效记录）
        redisUtils.hDelete(pendingKey, processedFields.toArray());

        return records.size();
    }
}
