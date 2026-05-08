package com.lsstop.service.impl;

import com.lsstop.constant.RedisConst;
import com.lsstop.domain.entity.WebsiteConfigEntity;
import com.lsstop.domain.vo.VisitStatsVO;
import com.lsstop.mapper.UniqueViewMapper;
import com.lsstop.mapper.WebsiteConfigMapper;
import com.lsstop.service.WebsiteConfigService;
import com.lsstop.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * 网站配置服务实现类
 *
 * @author lishusheng
 * @date 2025/12/25
 */
@Service
public class WebsiteConfigServiceImpl implements WebsiteConfigService {

    @Resource
    private WebsiteConfigMapper websiteConfigMapper;

    @Resource
    private UniqueViewMapper uniqueViewMapper;

    @Resource
    private RedisUtils redisUtils;

    /**
     * 获取网站配置信息，优先从Redis获取，没有则查DB并缓存
     *
     * @return 网站配置实体对象
     */
    @Override
    public WebsiteConfigEntity getWebsiteConfig() {
        // 优先从Redis获取
        WebsiteConfigEntity config = redisUtils.get(RedisConst.WEBSITE_CONFIG, WebsiteConfigEntity.class);
        if (config != null) {
            return config;
        }
        // Redis中没有，查询DB
        config = websiteConfigMapper.getWebsiteConfig();
        if (config != null) {
            // 缓存到1天
            redisUtils.set(RedisConst.WEBSITE_CONFIG, config, RedisConst.EXPIRE_ONE_DAY);
        }
        return config;
    }

    /**
     * 上报访问并获取访问统计
     * 同一IP每3小时计数一次
     *
     * @param ipAddress 访客IP地址
     * @return 访问统计信息
     */
    @Override
    public VisitStatsVO reportVisit(String ipAddress) {
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String uvKey = RedisConst.UNIQUE_VISITOR + ipAddress;

        // 检查该IP是否3小时内已访问，未访问则增加访问量
        Boolean isNew = redisUtils.setIfAbsent(uvKey, 1, 3 * RedisConst.EXPIRE_ONE_HOUR, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(isNew)) {
            // 增加今日访问量，返回1说明是首次创建，设置过期时间
            String todayKey = RedisConst.TODAY_VIEW_COUNT + today;
            Long count = redisUtils.increment(todayKey);
            if (count != null && count == 1) {
                redisUtils.expire(todayKey, RedisConst.EXPIRE_ONE_DAY + 2 * 3600);
            }
        }

        // 记录今日独立访客（每个IP每天只计一次）
        String uvSetKey = RedisConst.TODAY_UV_SET + today;
        Long uvAdded = redisUtils.sAdd(uvSetKey, ipAddress);
        if (uvAdded != null && uvAdded > 0) {
            // 设置过期时间（幂等操作，重复设置无副作用）
            redisUtils.expire(uvSetKey, RedisConst.EXPIRE_ONE_DAY + 2 * 3600);
        }

        // 获取历史总访问量，优先从Redis取
        Integer historyCount = redisUtils.get(RedisConst.HISTORY_VIEW_COUNT, Integer.class);
        if (historyCount == null) {
            historyCount = uniqueViewMapper.getTotalViewsCount();
            if (historyCount == null) {
                historyCount = 0;
            }
            redisUtils.set(RedisConst.HISTORY_VIEW_COUNT, historyCount);
        }

        // 获取今日访问量
        Integer todayCount = redisUtils.get(RedisConst.TODAY_VIEW_COUNT + today, Integer.class);
        if (todayCount == null) {
            todayCount = 0;
        }

        // 获取今日独立访客数
        Long todayUvCount = redisUtils.sSize(RedisConst.TODAY_UV_SET + today);
        Integer todayUv = (todayUvCount != null) ? todayUvCount.intValue() : 0;

        return VisitStatsVO.builder()
                .viewsCount(historyCount + todayCount)
                .todayUniqueVisitorCount(todayUv)
                .build();
    }

}
