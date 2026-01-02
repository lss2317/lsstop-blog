package com.lsstop.task;

import com.lsstop.constant.RedisConst;
import com.lsstop.domain.dto.TalkStatsDto;
import com.lsstop.mapper.TalkMapper;
import com.lsstop.utils.RedisUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 说说统计数据初始化
 * <p>项目启动时从数据库统计点赞数和评论数到Redis</p>
 *
 * @author lishusheng
 * @date 2026/01/02
 */
@Component
public class TalkStatsTask {

    private static final Logger log = LoggerFactory.getLogger(TalkStatsTask.class);

    @Resource
    private TalkMapper talkMapper;

    @Resource
    private RedisUtils redisUtils;

    /**
     * 项目启动时从数据库统计点赞数和评论数到Redis
     */
    @PostConstruct
    public void init() {
        log.info("开始初始化说说统计数据到Redis...");
        List<TalkStatsDto> statsList = talkMapper.countTalkStats();
        if (statsList == null || statsList.isEmpty()) {
            log.info("没有说说数据需要初始化");
            return;
        }
        for (TalkStatsDto stats : statsList) {
            redisUtils.set(RedisConst.TALK_LIKE_COUNT + stats.getId(), stats.getLikeCount());
            redisUtils.set(RedisConst.TALK_COMMENT_COUNT + stats.getId(), stats.getCommentCount());
        }
        log.info("说说统计数据初始化完成，共{}条", statsList.size());
    }
}
