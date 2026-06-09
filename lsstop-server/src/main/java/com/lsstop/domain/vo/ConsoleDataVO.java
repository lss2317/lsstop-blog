package com.lsstop.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Console 主页聚合响应
 *
 * @author lishusheng
 * @date 2026/05/08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsoleDataVO {

    /**
     * 统计卡片列表
     */
    private List<StatCardItem> statCards;

    /**
     * 近七天评论统计
     */
    private CommentStatVO commentStat;

    /**
     * 近十天访问量
     */
    private VisitOverviewVO visitOverview;

    /**
     * 最近评论（limit=5）
     */
    private List<RecentCommentItem> recentComments;

    /**
     * 待审核统计
     */
    private PendingReviewVO pendingReview;

    /**
     * 内容概览
     */
    private ContentOverviewVO contentOverview;

    /**
     * 统计卡片项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatCardItem {
        /**
         * 卡片标识（totalVisits/totalUsers/totalComments/totalMessages）
         */
        private String key;
        /**
         * 累计数值
         */
        private Integer num;
        /**
         * 今日新增
         */
        private Integer todayCount;
    }

    /**
     * 每日统计项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyStatItem {
        /**
         * 日期（yyyy-MM-dd 格式）
         */
        private String date;
        /**
         * 当日数值
         */
        private Integer count;
    }

    /**
     * 近七天评论统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentStatVO {
        /**
         * 近7天每日评论数
         */
        private List<DailyStatItem> dailyStats;
        /**
         * 7天评论总数
         */
        private Integer totalCount;
        /**
         * 今日新增
         */
        private Integer todayCount;
        /**
         * 日均评论
         */
        private Integer dailyAvg;
        /**
         * 周同比（如"+15%"）
         */
        private String weekOverWeek;
    }

    /**
     * 近十天访问量
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VisitOverviewVO {
        /**
         * 近10天每日访问量
         */
        private List<DailyStatItem> dailyStats;
    }

    /**
     * 最近评论项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentCommentItem {
        /**
         * 头像
         */
        private String avatar;
        /**
         * 昵称
         */
        private String nickname;
        /**
         * 评论内容
         */
        private String content;
        /**
         * 评论目标类型：1=文章, 2=友链, 3=说说
         */
        private Integer targetType;
        /**
         * 目标名称（文章标题/说说内容摘要；友链时为空字符串）
         */
        private String targetName;
        /**
         * 评论时间
         */
        private String createdAt;
    }

    /**
     * 待审核统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PendingReviewVO {
        /**
         * 待审核评论数
         */
        private Integer commentCount;
        /**
         * 待审核留言数
         */
        private Integer messageCount;
    }

    /**
     * 内容概览
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContentOverviewVO {
        /**
         * 文章总数
         */
        private Integer articleCount;
        /**
         * 分类总数
         */
        private Integer categoryCount;
        /**
         * 标签总数
         */
        private Integer tagCount;
        /**
         * 友链总数
         */
        private Integer friendLinkCount;
    }
}
