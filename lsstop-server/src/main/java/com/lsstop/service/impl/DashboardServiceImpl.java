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
        LocalDate today = LocalDate.now();

        return AnalysisDataVO.builder()
                .uniqueVisitorTrend(buildUniqueVisitorTrend(today))
                .topArticles(getCachedTopArticles())
                .categoryDistribution(getCachedCategoryDistribution())
                .commentSource(getCachedCommentSource())
                .interactionTrend(buildInteractionTrend(today))
                .tagRadar(getCachedTagRadar())
                .build();
    }

    /**
     * 构建统计卡片数据
     */
    private List<ConsoleDataVO.StatCardItem> buildStatCards(LocalDate today) {
        List<ConsoleDataVO.StatCardItem> cards = new ArrayList<>();
        String todayStr = today.format(DateTimeFormatter.BASIC_ISO_DATE);

        // 总访问量
        Integer totalVisits = getTotalViewsFromRedis(today);
        Integer todayVisits = redisUtils.get(RedisConst.TODAY_VIEW_COUNT + todayStr, Integer.class);
        cards.add(buildStatCard(DashboardConst.STAT_KEY_TOTAL_VISITS, totalVisits, todayVisits != null ? todayVisits : 0));

        // 总用户数（缓存1小时，注册频率低）
        Integer totalUsers = redisUtils.getOrLoad(RedisConst.TOTAL_USER_COUNT, Integer.class,
                dashboardMapper::getTotalUserCount, RedisConst.EXPIRE_ONE_HOUR);
        Integer todayUsers = redisUtils.get(RedisConst.TODAY_USER_COUNT + todayStr, Integer.class);
        cards.add(buildStatCard(DashboardConst.STAT_KEY_TOTAL_USERS, totalUsers, todayUsers != null ? todayUsers : 0));

        // 总评论数（缓存5分钟兜底，新增/删除时主动清除）
        Integer totalComments = redisUtils.getOrLoad(RedisConst.TOTAL_COMMENT_COUNT, Integer.class,
                dashboardMapper::getTotalCommentCount, RedisConst.EXPIRE_FIVE_MINUTES);
        Integer todayComments = redisUtils.get(RedisConst.TODAY_COMMENT_COUNT + todayStr, Integer.class);
        cards.add(buildStatCard(DashboardConst.STAT_KEY_TOTAL_COMMENTS, totalComments, todayComments != null ? todayComments : 0));

        // 总留言数（缓存5分钟兜底，新增时主动清除）
        Integer totalMessages = redisUtils.getOrLoad(RedisConst.TOTAL_MESSAGE_COUNT, Integer.class,
                dashboardMapper::getTotalMessageCount, RedisConst.EXPIRE_FIVE_MINUTES);
        Integer todayMessages = redisUtils.get(RedisConst.TODAY_MESSAGE_COUNT + todayStr, Integer.class);
        cards.add(buildStatCard(DashboardConst.STAT_KEY_TOTAL_MESSAGES, totalMessages, todayMessages != null ? todayMessages : 0));

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
    private ConsoleDataVO.StatCardItem buildStatCard(String key, Integer num, Integer todayCount) {
        return ConsoleDataVO.StatCardItem.builder().key(key).num(num).todayCount(todayCount).build();
    }

    /**
     * 从 Redis 获取总访问量（历史总量 + 昨日未同步量 + 今日访问量）
     */
    private Integer getTotalViewsFromRedis(LocalDate today) {
        Integer historyCount = redisUtils.get(RedisConst.HISTORY_VIEW_COUNT, Integer.class);
        if (historyCount == null) {
            historyCount = uniqueViewMapper.getTotalViewsCount();
            if (historyCount == null) {
                historyCount = 0;
            }
        }
        // 补充昨日未同步的访问量（解决凌晨00:00~01:00数据窗口问题）
        String yesterdayStr = today.minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE);
        Integer yesterdayCount = redisUtils.get(RedisConst.TODAY_VIEW_COUNT + yesterdayStr, Integer.class);
        if (yesterdayCount != null) {
            historyCount = historyCount + yesterdayCount;
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
                // 历史日期未命中：先尝试从todayPrefix读取（可能定时任务尚未同步）
                String dateStr = dates.get(i).format(DateTimeFormatter.BASIC_ISO_DATE);
                Integer unsyncedCount = redisUtils.get(todayPrefix + dateStr, Integer.class);
                if (unsyncedCount != null) {
                    result.add(unsyncedCount);
                } else {
                    // todayPrefix也没有，查库并缓存
                    int dbCount = loadHistoryDailyCount(dates.get(i), historyPrefix);
                    result.add(dbCount);
                }
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
        switch (keyPrefix) {
            case RedisConst.DAILY_COMMENT_COUNT -> count = dashboardMapper.getCommentCountByDate(date);
            case RedisConst.DAILY_MESSAGE_COUNT -> count = dashboardMapper.getMessageCountByDate(date);
            case RedisConst.DAILY_LIKE_COUNT -> count = dashboardMapper.getLikeCountByDate(date);
            default -> {
                var record = uniqueViewMapper.getByViewDate(date);
                count = (record != null && record.getViewsCount() != null) ? record.getViewsCount() : 0;
            }
        }
        redisUtils.set(cacheKey, count, RedisConst.EXPIRE_ONE_WEEK);
        return count;
    }

    /**
     * 构建近30天独立访客趋势（历史日期走Redis批量+DB兜底，今日走UV Set）
     */
    private AnalysisDataVO.UniqueVisitorTrendVO buildUniqueVisitorTrend(LocalDate today) {
        LocalDate startDate = today.minusDays(DashboardConst.DAYS_30 - 1);

        // 批量获取历史日期的独立访客数（Redis缓存 + DB兜底）
        List<AnalysisDataVO.DailyStatItem> dailyStats = new ArrayList<>(DashboardConst.DAYS_30);
        for (int i = 0; i < DashboardConst.DAYS_30; i++) {
            LocalDate date = startDate.plusDays(i);
            int count;
            if (date.equals(today)) {
                // 今日独立访客数从UV Set取
                Long uvSize = redisUtils.sSize(RedisConst.TODAY_UV_SET + today.format(DateTimeFormatter.BASIC_ISO_DATE));
                count = uvSize != null ? uvSize.intValue() : 0;
            } else {
                // 历史日期：先查Redis缓存，未命中查库并缓存
                String key = RedisConst.DAILY_UV_COUNT + date.format(DateTimeFormatter.BASIC_ISO_DATE);
                Integer cached = redisUtils.get(key, Integer.class);
                if (cached != null) {
                    count = cached;
                } else {
                    Integer dbCount = dashboardMapper.getUniqueVisitorCountByDate(date);
                    count = dbCount != null ? dbCount : 0;
                    redisUtils.set(key, count, RedisConst.EXPIRE_ONE_WEEK);
                }
            }
            dailyStats.add(AnalysisDataVO.DailyStatItem.builder()
                    .date(date.format(DATE_FMT))
                    .count(count)
                    .build());
        }
        return AnalysisDataVO.UniqueVisitorTrendVO.builder().dailyStats(dailyStats).build();
    }

    /**
     * 构建近7天互动趋势（评论、留言、点赞，全部走Redis批量+DB兜底）
     */
    private AnalysisDataVO.InteractionTrendVO buildInteractionTrend(LocalDate today) {
        // 批量获取近7天评论/留言/点赞数（Redis缓存 + DB兜底）
        List<Integer> commentCounts = batchGetDailyCounts(today, DashboardConst.DAYS_7,
                RedisConst.TODAY_COMMENT_COUNT, RedisConst.DAILY_COMMENT_COUNT);
        List<Integer> messageCounts = batchGetDailyCounts(today, DashboardConst.DAYS_7,
                RedisConst.TODAY_MESSAGE_COUNT, RedisConst.DAILY_MESSAGE_COUNT);
        List<Integer> likeCounts = batchGetDailyCounts(today, DashboardConst.DAYS_7,
                RedisConst.TODAY_LIKE_COUNT, RedisConst.DAILY_LIKE_COUNT);

        List<AnalysisDataVO.DailyInteractionItem> dailyData = new ArrayList<>(DashboardConst.DAYS_7);
        for (int i = 0; i < DashboardConst.DAYS_7; i++) {
            LocalDate date = today.minusDays(DashboardConst.DAYS_7 - 1 - i);
            dailyData.add(AnalysisDataVO.DailyInteractionItem.builder()
                    .date(date.format(DATE_FMT))
                    .comment(commentCounts.get(i))
                    .message(messageCounts.get(i))
                    .like(likeCounts.get(i))
                    .build());
        }
        return AnalysisDataVO.InteractionTrendVO.builder().dailyData(dailyData).build();
    }

    /**
     * 获取热门文章（缓存1小时，浏览量通过定时任务同步）
     */
    private List<AnalysisDataVO.TopArticleItem> getCachedTopArticles() {
        List<AnalysisDataVO.TopArticleItem> cached = redisUtils.getList(
                RedisConst.DASHBOARD_TOP_ARTICLES, AnalysisDataVO.TopArticleItem.class);
        if (cached != null) {
            return cached;
        }
        List<AnalysisDataVO.TopArticleItem> result = dashboardMapper.getTopArticles(DashboardConst.TOP_ARTICLE_LIMIT);
        redisUtils.set(RedisConst.DASHBOARD_TOP_ARTICLES, result, RedisConst.EXPIRE_ONE_HOUR);
        return result;
    }

    /**
     * 获取文章分类分布（缓存1小时，仅后台管理操作才变化）
     */
    private List<AnalysisDataVO.CategoryDistributionItem> getCachedCategoryDistribution() {
        List<AnalysisDataVO.CategoryDistributionItem> cached = redisUtils.getList(
                RedisConst.DASHBOARD_CATEGORY_DISTRIBUTION, AnalysisDataVO.CategoryDistributionItem.class);
        if (cached != null) {
            return cached;
        }
        List<AnalysisDataVO.CategoryDistributionItem> result = dashboardMapper.getCategoryDistribution();
        redisUtils.set(RedisConst.DASHBOARD_CATEGORY_DISTRIBUTION, result, RedisConst.EXPIRE_ONE_HOUR);
        return result;
    }

    /**
     * 获取评论来源分布（缓存5分钟，评论实时变化）
     */
    private List<AnalysisDataVO.CommentSourceItem> getCachedCommentSource() {
        List<AnalysisDataVO.CommentSourceItem> cached = redisUtils.getList(
                RedisConst.DASHBOARD_COMMENT_SOURCE, AnalysisDataVO.CommentSourceItem.class);
        if (cached != null) {
            return cached;
        }
        List<AnalysisDataVO.CommentSourceItem> result = dashboardMapper.getCommentSourceDistribution();
        redisUtils.set(RedisConst.DASHBOARD_COMMENT_SOURCE, result, RedisConst.EXPIRE_FIVE_MINUTES);
        return result;
    }

    /**
     * 获取标签热度（缓存1小时，仅后台管理操作才变化）
     */
    private List<AnalysisDataVO.TagRadarItem> getCachedTagRadar() {
        List<AnalysisDataVO.TagRadarItem> cached = redisUtils.getList(
                RedisConst.DASHBOARD_TAG_RADAR, AnalysisDataVO.TagRadarItem.class);
        if (cached != null) {
            return cached;
        }
        List<AnalysisDataVO.TagRadarItem> result = dashboardMapper.getTagRadar(DashboardConst.TAG_RADAR_LIMIT);
        redisUtils.set(RedisConst.DASHBOARD_TAG_RADAR, result, RedisConst.EXPIRE_ONE_HOUR);
        return result;
    }
}
