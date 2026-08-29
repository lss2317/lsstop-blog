package com.lsstop.task;

import com.lsstop.constant.RedisConst;
import com.lsstop.domain.entity.UniqueViewEntity;
import com.lsstop.mapper.UniqueViewMapper;
import com.lsstop.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 网站访问量统计任务
 * <p>每天凌晨将前一天的访问量同步到数据库</p>
 *
 * @author lishusheng
 * @date 2026/02/19
 */
@Component
public class UniqueViewTask {

    private static final Logger log = LoggerFactory.getLogger(UniqueViewTask.class);

    @Resource
    private UniqueViewMapper uniqueViewMapper;

    @Resource
    private RedisUtils redisUtils;

    /**
     * 每天凌晨1点同步前一天的访问量到数据库
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void syncYesterdayViewCount() {
        log.info("开始同步昨日访问量到数据库...");
        try {
            LocalDate syncDate = LocalDate.now();
            LocalDate yesterday = syncDate.minusDays(1);
            String yesterdayStr = yesterday.format(DateTimeFormatter.BASIC_ISO_DATE);
            String countKey = RedisConst.TODAY_VIEW_COUNT + yesterdayStr;
            String uvSetKey = RedisConst.TODAY_UV_SET + yesterdayStr;

            // 获取昨日访问量
            Integer viewsCount = redisUtils.get(countKey, Integer.class);
            if (viewsCount == null) {
                viewsCount = 0;
            }

            // 获取昨日独立访客数
            Long uvCount = redisUtils.sSize(uvSetKey);
            Integer uniqueVisitorCount = (uvCount != null) ? uvCount.intValue() : 0;

            // 检查是否已存在记录
            UniqueViewEntity existing = uniqueViewMapper.getByViewDate(yesterday);
            if (existing != null) {
                // 更新记录
                uniqueViewMapper.updateViewsCount(yesterday, viewsCount, uniqueVisitorCount);
                log.info("更新昨日访问量记录: {}, 访问量: {}, 独立访客: {}", yesterday, viewsCount, uniqueVisitorCount);
            } else {
                // 新增记录
                UniqueViewEntity entity = UniqueViewEntity.builder()
                        .viewDate(yesterday)
                        .viewsCount(viewsCount)
                        .uniqueVisitorCount(uniqueVisitorCount)
                        .build();
                uniqueViewMapper.insert(entity);
                log.info("新增昨日访问量记录: {}, 访问量: {}, 独立访客: {}", yesterday, viewsCount, uniqueVisitorCount);
            }

            // 删除已同步的Redis数据
            redisUtils.delete(countKey);
            redisUtils.delete(uvSetKey);
            // 刷新历史总访问量缓存
            redisUtils.delete(RedisConst.HISTORY_VIEW_COUNT);
            // 今日的30天UV历史区间已包含昨日，必须清除可能在同步前生成的缓存
            redisUtils.delete(RedisConst.DASHBOARD_UV_HISTORY
                    + syncDate.format(DateTimeFormatter.BASIC_ISO_DATE));
        } catch (Exception e) {
            log.error("同步昨日访问量失败", e);
        }
    }
}
