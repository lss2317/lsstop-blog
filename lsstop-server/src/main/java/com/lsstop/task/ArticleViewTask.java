package com.lsstop.task;

import com.lsstop.constant.RedisConst;
import com.lsstop.domain.vo.ArticleViewCountVO;
import com.lsstop.mapper.ArticleMapper;
import com.lsstop.utils.RedisUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 文章访问量统计任务
 * <p>项目启动时初始化访问量到Redis，定时同步访问量到数据库</p>
 *
 * @author lishusheng
 * @date 2026/01/26
 */
@Component
public class ArticleViewTask {

    private static final Logger log = LoggerFactory.getLogger(ArticleViewTask.class);

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private RedisUtils redisUtils;

    /**
     * 项目启动时初始化文章访问量到Redis
     */
    @PostConstruct
    public void init() {
        clearOldData();
        initArticleViewCounts();
    }

    /**
     * 清理Redis中的旧访问量数据
     */
    private void clearOldData() {
        log.info("开始清理Redis中的旧访问量数据...");
        redisUtils.deleteByPrefix(RedisConst.ARTICLE_VIEW_COUNT);
        log.info("旧访问量数据清理完成");
    }

    /**
     * 初始化文章访问量到Redis
     */
    private void initArticleViewCounts() {
        log.info("开始初始化文章访问量到Redis...");
        List<ArticleViewCountVO> viewCounts = articleMapper.listAllArticleViewCounts();
        if (viewCounts == null || viewCounts.isEmpty()) {
            log.info("没有文章访问量需要初始化");
            return;
        }
        for (ArticleViewCountVO viewCount : viewCounts) {
            if (viewCount.getViewCount() != null && viewCount.getViewCount() > 0) {
                redisUtils.set(RedisConst.ARTICLE_VIEW_COUNT + viewCount.getId(), viewCount.getViewCount());
            }
        }
        log.info("文章访问量初始化完成，共{}条", viewCounts.size());
    }

    /**
     * 定时同步文章访问量到数据库
     * 每10分钟执行一次，首次延迟10分钟执行避免与初始化冲突
     */
    @Scheduled(fixedDelay = 10 * 60 * 1000, initialDelay = 10 * 60 * 1000)
    public void syncArticleViewCountsToDb() {
        log.info("开始同步文章访问量到数据库...");
        try {
            // 获取所有文章的访问量key
            Set<String> keys = redisUtils.keys(RedisConst.ARTICLE_VIEW_COUNT + "*");
            if (keys == null || keys.isEmpty()) {
                log.info("没有文章访问量需要同步");
                return;
            }

            List<ArticleViewCountVO> viewCounts = new ArrayList<>();
            for (String key : keys) {
                // 提取文章ID
                String idStr = key.substring(RedisConst.ARTICLE_VIEW_COUNT.length());
                // 过滤非数字key（如访问记录key）
                if (idStr.contains(":")) {
                    continue;
                }
                try {
                    Integer id = Integer.parseInt(idStr);
                    Integer count = redisUtils.get(key, Integer.class);
                    if (count != null && count > 0) {
                        ArticleViewCountVO vo = new ArticleViewCountVO();
                        vo.setId(id);
                        vo.setViewCount(count);
                        viewCounts.add(vo);
                    }
                } catch (NumberFormatException e) {
                    log.warn("无效的文章访问量key: {}", key);
                }
            }

            if (!viewCounts.isEmpty()) {
                articleMapper.batchUpdateViewCounts(viewCounts);
                log.info("文章访问量同步完成，共{}条", viewCounts.size());
            }
        } catch (Exception e) {
            log.error("同步文章访问量失败", e);
        }
    }
}
