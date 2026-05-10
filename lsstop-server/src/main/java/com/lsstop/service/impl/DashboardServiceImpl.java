package com.lsstop.service.impl;

import com.lsstop.constant.DashboardConst;
import com.lsstop.constant.RedisConst;
import com.lsstop.domain.vo.AnalysisDataVO;
import com.lsstop.domain.vo.ConsoleDataVO;
import com.lsstop.mapper.ArticleMapper;
import com.lsstop.mapper.DashboardMapper;
import com.lsstop.mapper.UniqueViewMapper;
import com.lsstop.service.DashboardService;
import com.lsstop.utils.MathUtils;
import com.lsstop.utils.RedisUtils;
import com.lsstop.utils.StringUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 仪表盘服务实现
 *
 * @author lishusheng
 * @date 2026/05/08
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Resource
    private DashboardMapper dashboardMapper;

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private UniqueViewMapper uniqueViewMapper;

    @Resource
    private RedisUtils redisUtils;

    @Override
    public ConsoleDataVO getConsoleData() {
        LocalDate today = LocalDate.now();

        return ConsoleDataVO.builder()
                .statCards(buildStatCards(today))
                .commentStat(buildCommentStat(today))
                .visitOverview(buildVisitOverview(today))
                .recentComments(dashboardMapper.getRecentComments(DashboardConst.RECENT_COMMENT_LIMIT))
                .pendingReview(buildPendingReview())
                .contentOverview(buildContentOverview())
                .build();
    }

    @Override
    public AnalysisDataVO getAnalysisData() {
        return null;
    }

    /**
     * 构建统计卡片数据
     */
    private List<ConsoleDataVO.StatCardItem> buildStatCards(LocalDate today) {
        List<ConsoleDataVO.StatCardItem> cards = new ArrayList<>();

        // 总访问量
        Integer totalVisits = getTotalViewsFromRedis(today);
        cards.add(buildStatCard(DashboardConst.STAT_KEY_TOTAL_VISITS, totalVisits));

        // 总用户数（缓存1小时，注册频率低）
        Integer totalUsers = redisUtils.getOrLoad(RedisConst.TOTAL_USER_COUNT, Integer.class,
                dashboardMapper::getTotalUserCount, RedisConst.EXPIRE_ONE_HOUR);
        cards.add(buildStatCard(DashboardConst.STAT_KEY_TOTAL_USERS, totalUsers));

        // 总评论数（缓存5分钟兜底，新增/删除时主动清除）
        Integer totalComments = redisUtils.getOrLoad(RedisConst.TOTAL_COMMENT_COUNT, Integer.class,
                dashboardMapper::getTotalCommentCount, RedisConst.EXPIRE_FIVE_MINUTES);
        cards.add(buildStatCard(DashboardConst.STAT_KEY_TOTAL_COMMENTS, totalComments));

        // 总留言数（缓存5分钟兜底，新增时主动清除）
        Integer totalMessages = redisUtils.getOrLoad(RedisConst.TOTAL_MESSAGE_COUNT, Integer.class,
                dashboardMapper::getTotalMessageCount, RedisConst.EXPIRE_FIVE_MINUTES);
        cards.add(buildStatCard(DashboardConst.STAT_KEY_TOTAL_MESSAGES, totalMessages));

        return cards;
    }

    /**
     * 构建近七天评论统计
     */
    private ConsoleDataVO.CommentStatVO buildCommentStat(LocalDate today) {
        LocalDate lastWeekEnd = today.minusDays(DashboardConst.DAYS_7);

        // 批量获取近七天评论数
        List<Integer> thisWeekCounts = batchGetDailyCounts(today, DashboardConst.DAYS_7,
                RedisConst.TODAY_COMMENT_COUNT, RedisConst.DAILY_COMMENT_COUNT);

        // 组装每日统计
        List<ConsoleDataVO.DailyStatItem> dailyStats = new ArrayList<>(DashboardConst.DAYS_7);
        for (int i = 0; i < DashboardConst.DAYS_7; i++) {
            LocalDate date = today.minusDays(DashboardConst.DAYS_7 - 1 - i);
            dailyStats.add(ConsoleDataVO.DailyStatItem.builder()
                    .date(date.format(DATE_FMT))
                    .count(thisWeekCounts.get(i))
                    .build());
        }

        int totalCount = thisWeekCounts.stream().mapToInt(Integer::intValue).sum();
        int todayCount = thisWeekCounts.get(thisWeekCounts.size() - 1);
        int dailyAvg = totalCount / DashboardConst.DAYS_7;

        // 周同比：批量获取上周数据（全部为历史日期）
        List<Integer> lastWeekCounts = batchGetDailyCounts(lastWeekEnd, DashboardConst.DAYS_7,
                RedisConst.TODAY_COMMENT_COUNT, RedisConst.DAILY_COMMENT_COUNT);
        int lastWeekCount = lastWeekCounts.stream().mapToInt(Integer::intValue).sum();

        return ConsoleDataVO.CommentStatVO.builder()
                .dailyStats(dailyStats)
                .totalCount(totalCount)
                .todayCount(todayCount)
                .dailyAvg(dailyAvg)
                .weekOverWeek(StringUtils.formatPercent(MathUtils.calcChangePercent(totalCount, lastWeekCount)))
                .build();
    }

    /**
     * 构建近十天访问量
     */
    private ConsoleDataVO.VisitOverviewVO buildVisitOverview(LocalDate today) {
        // 批量获取近十天访问量
        List<Integer> counts = batchGetDailyCounts(today, DashboardConst.DAYS_10,
                RedisConst.TODAY_VIEW_COUNT, RedisConst.DAILY_VIEW_COUNT);

        List<ConsoleDataVO.DailyStatItem> dailyStats = new ArrayList<>(DashboardConst.DAYS_10);
        for (int i = 0; i < DashboardConst.DAYS_10; i++) {
            LocalDate date = today.minusDays(DashboardConst.DAYS_10 - 1 - i);
            dailyStats.add(ConsoleDataVO.DailyStatItem.builder()
                    .date(date.format(DATE_FMT))
                    .count(counts.get(i))
                    .build());
        }
        return ConsoleDataVO.VisitOverviewVO.builder().dailyStats(dailyStats).build();
    }

    /**
     * 构建待审核统计
     */
    private ConsoleDataVO.PendingReviewVO buildPendingReview() {
        // 待审核评论数（缓存5分钟兜底，新增/删除时主动清除）
        Integer commentCount = redisUtils.getOrLoad(RedisConst.PENDING_REVIEW_COMMENT_COUNT, Integer.class,
                dashboardMapper::getPendingReviewCommentCount, RedisConst.EXPIRE_FIVE_MINUTES);
        // 待审核留言数
        Integer messageCount = redisUtils.getOrLoad(RedisConst.PENDING_REVIEW_MESSAGE_COUNT, Integer.class,
                dashboardMapper::getPendingReviewMessageCount, RedisConst.EXPIRE_FIVE_MINUTES);
        return ConsoleDataVO.PendingReviewVO.builder()
                .commentCount(commentCount)
                .messageCount(messageCount)
                .build();
    }

    /**
     * 构建内容概览
     */
    private ConsoleDataVO.ContentOverviewVO buildContentOverview() {
        // 文章总数（缓存1小时，仅后台管理操作才变化）
        Integer articleCount = redisUtils.getOrLoad(RedisConst.TOTAL_ARTICLE_COUNT, Integer.class,
                articleMapper::getArticleCount, RedisConst.EXPIRE_ONE_HOUR);
        // 分类总数
        Integer categoryCount = redisUtils.getOrLoad(RedisConst.TOTAL_CATEGORY_COUNT, Integer.class,
                dashboardMapper::getCategoryCount, RedisConst.EXPIRE_ONE_HOUR);
        // 标签总数
        Integer tagCount = redisUtils.getOrLoad(RedisConst.TOTAL_TAG_COUNT, Integer.class,
                dashboardMapper::getTagCount, RedisConst.EXPIRE_ONE_HOUR);
        // 友链总数
        Integer friendLinkCount = redisUtils.getOrLoad(RedisConst.TOTAL_FRIEND_LINK_COUNT, Integer.class,
                dashboardMapper::getFriendLinkCount, RedisConst.EXPIRE_ONE_HOUR);
        return ConsoleDataVO.ContentOverviewVO.builder()
                .articleCount(articleCount)
                .categoryCount(categoryCount)
                .tagCount(tagCount)
                .friendLinkCount(friendLinkCount)
                .build();
    }

    /**
     * 构建单个统计卡片
     */
    private ConsoleDataVO.StatCardItem buildStatCard(String key, Integer num) {
        return ConsoleDataVO.StatCardItem.builder().key(key).num(num).build();
    }

    /**
     * 从 Redis 获取总访问量（历史总量 + 今日访问量）
     */
    private Integer getTotalViewsFromRedis(LocalDate today) {
        Integer historyCount = redisUtils.get(RedisConst.HISTORY_VIEW_COUNT, Integer.class);
        if (historyCount == null) {
            historyCount = uniqueViewMapper.getTotalViewsCount();
            if (historyCount == null) {
                historyCount = 0;
            }
        }
        String todayStr = today.format(DateTimeFormatter.BASIC_ISO_DATE);
        Integer todayCount = redisUtils.get(RedisConst.TODAY_VIEW_COUNT + todayStr, Integer.class);
        return historyCount + (todayCount != null ? todayCount : 0);
    }

    /**
     * 批量获取以endDate为结束日的近N天每日计数（今天用实时key，历史用缓存key）
     * 返回顺序：从远到近
     */
    private List<Integer> batchGetDailyCounts(LocalDate endDate, int days, String todayPrefix, String historyPrefix) {
        LocalDate today = LocalDate.now();

        // 构建key列表
        List<String> keys = new ArrayList<>(days);
        List<LocalDate> dates = new ArrayList<>(days);
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = endDate.minusDays(i);
            dates.add(date);
            String dateStr = date.format(DateTimeFormatter.BASIC_ISO_DATE);
            keys.add(date.equals(today) ? todayPrefix + dateStr : historyPrefix + dateStr);
        }

        // 批量获取
        List<Integer> values = redisUtils.mGet(keys, Integer.class);

        // 处理未命中的历史日期：查库并缓存
        List<Integer> result = new ArrayList<>(days);
        for (int i = 0; i < days; i++) {
            Integer count = values.get(i);
            if (count != null) {
                result.add(count);
            } else if (dates.get(i).equals(today)) {
                // 今天无数据表示还没有访问/评论
                result.add(0);
            } else {
                // 历史日期未命中：查库并缓存
                int dbCount = loadHistoryDailyCount(dates.get(i), historyPrefix);
                result.add(dbCount);
            }
        }
        return result;
    }

    /**
     * 查库加载历史日期的每日计数并缓存
     */
    private int loadHistoryDailyCount(LocalDate date, String keyPrefix) {
        String cacheKey = keyPrefix + date.format(DateTimeFormatter.BASIC_ISO_DATE);
        int count;
        if (keyPrefix.equals(RedisConst.DAILY_COMMENT_COUNT)) {
            count = dashboardMapper.getCommentCountByDate(date);
        } else {
            var record = uniqueViewMapper.getByViewDate(date);
            count = (record != null && record.getViewsCount() != null) ? record.getViewsCount() : 0;
        }
        redisUtils.set(cacheKey, count, RedisConst.EXPIRE_ONE_WEEK);
        return count;
    }
}
