package com.lsstop.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Analysis 分析页聚合响应
 *
 * @author lishusheng
 * @date 2026/05/08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisDataVO {

    /**
     * 近30天独立访客趋势
     */
    private UniqueVisitorTrendVO uniqueVisitorTrend;

    /**
     * 热门文章 Top7
     */
    private List<TopArticleItem> topArticles;

    /**
     * 文章分类分布
     */
    private List<CategoryDistributionItem> categoryDistribution;

    /**
     * 评论来源分布
     */
    private List<CommentSourceItem> commentSource;

    /**
     * 近7天互动趋势
     */
    private InteractionTrendVO interactionTrend;

    /**
     * 标签热度
     */
    private List<TagRadarItem> tagRadar;

    /**
     * 独立访客趋势
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UniqueVisitorTrendVO {
        /**
         * 近30天每日独立访客数
         */
        private List<DailyStatItem> dailyStats;
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
     * 热门文章项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopArticleItem {
        /**
         * 文章标题
         */
        private String name;
        /**
         * 浏览量
         */
        private Integer viewCount;
    }

    /**
     * 分类分布项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryDistributionItem {
        /**
         * 分类名
         */
        private String name;
        /**
         * 文章数
         */
        private Integer value;
    }

    /**
     * 评论来源分布项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentSourceItem {
        /**
         * 评论目标类型：1=文章 2=友链 3=说说
         */
        private Integer targetType;
        /**
         * 评论数
         */
        private Integer value;
    }

    /**
     * 互动趋势
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InteractionTrendVO {
        /**
         * 近7天每日互动数据
         */
        private List<DailyInteractionItem> dailyData;
    }

    /**
     * 单日互动数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyInteractionItem {
        /**
         * 日期（yyyy-MM-dd 格式）
         */
        private String date;
        /**
         * 评论数
         */
        private Integer comment;
        /**
         * 留言数
         */
        private Integer message;
        /**
         * 点赞数
         */
        private Integer like;
    }

    /**
     * 标签热度项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TagRadarItem {
        /**
         * 标签名
         */
        private String name;
        /**
         * 文章数
         */
        private Integer value;
    }
}
