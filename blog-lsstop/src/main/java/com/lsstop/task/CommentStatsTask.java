package com.lsstop.task;

import com.lsstop.constant.RedisConst;
import com.lsstop.domain.vo.CommentCountVO;
import com.lsstop.enums.CommentTypeEnum;
import com.lsstop.mapper.CommentMapper;
import com.lsstop.utils.RedisUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 评论统计任务
 * <p>项目启动时初始化评论数据到Redis</p>
 *
 * @author lishusheng
 * @date 2026/01/26
 */
@Component
public class CommentStatsTask {

    private static final Logger log = LoggerFactory.getLogger(CommentStatsTask.class);

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private RedisUtils redisUtils;

    /**
     * 项目启动时初始化评论数据到Redis
     */
    @PostConstruct
    public void init() {
        clearOldData();
        initCommentCounts();
    }

    /**
     * 清理Redis中的旧评论数据
     */
    private void clearOldData() {
        log.info("开始清理Redis中的旧评论数据...");
        redisUtils.deleteByPrefix(RedisConst.TALK_COMMENT_COUNT);
        log.info("旧评论数据清理完成");
    }

    /**
     * 初始化说说评论数
     */
    private void initCommentCounts() {
        log.info("开始初始化说说评论数到Redis...");
        // 只有说说需要评论数
        List<CommentCountVO> commentCounts = commentMapper.countCommentsByTargetType(CommentTypeEnum.TALK.getType());
        if (commentCounts == null || commentCounts.isEmpty()) {
            log.info("没有说说评论数需要初始化");
            return;
        }
        for (CommentCountVO commentCount : commentCounts) {
            redisUtils.set(RedisConst.TALK_COMMENT_COUNT + commentCount.getTargetId(), commentCount.getCommentCount());
        }
        log.info("说说评论数初始化完成，共{}条", commentCounts.size());
    }
}
