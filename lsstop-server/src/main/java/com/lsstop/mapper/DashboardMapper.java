package com.lsstop.mapper;

import com.lsstop.domain.vo.AnalysisDataVO;
import com.lsstop.domain.vo.ConsoleDataVO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 仪表盘数据访问层
 *
 * @author lishusheng
 * @date 2026/05/08
 */
public interface DashboardMapper {

    /**
     * 查询用户总数
     */
    Integer getTotalUserCount();

    /**
     * 查询评论总数
     */
    Integer getTotalCommentCount();

    /**
     * 查询留言总数
     */
    Integer getTotalMessageCount();

    /**
     * 查询指定日期的评论数
     */
    Integer getCommentCountByDate(@Param("date") LocalDate date);

    /**
     * 查询每日评论统计
     */
    List<ConsoleDataVO.DailyStatItem> getDailyCommentStats(@Param("startTime") LocalDateTime startTime,
                                                          @Param("endTime") LocalDateTime endTime);

    /**
     * 查询每日留言统计
     */
    List<ConsoleDataVO.DailyStatItem> getDailyMessageStats(@Param("startTime") LocalDateTime startTime,
                                                          @Param("endTime") LocalDateTime endTime);

    /**
     * 查询最近评论
     */
    List<ConsoleDataVO.RecentCommentItem> getRecentComments(@Param("limit") Integer limit);

    /**
     * 查询待审核评论数
     */
    Integer getPendingReviewCommentCount();

    /**
     * 查询待审核留言数
     */
    Integer getPendingReviewMessageCount();

    /**
     * 查询分类总数
     */
    Integer getCategoryCount();

    /**
     * 查询标签总数
     */
    Integer getTagCount();

    /**
     * 查询友链总数
     */
    Integer getFriendLinkCount();

    /**
     * 查询热门文章 Top N（按浏览量降序）
     */
    List<AnalysisDataVO.TopArticleItem> getTopArticles(@Param("limit") Integer limit);

    /**
     * 查询文章分类分布
     */
    List<AnalysisDataVO.CategoryDistributionItem> getCategoryDistribution();

    /**
     * 查询评论来源分布
     */
    List<AnalysisDataVO.CommentSourceItem> getCommentSourceDistribution();

    /**
     * 查询指定日期的留言数
     */
    Integer getMessageCountByDate(@Param("date") LocalDate date);

    /**
     * 查询指定日期的点赞数
     */
    Integer getLikeCountByDate(@Param("date") LocalDate date);

    /**
     * 查询指定日期的独立访客数
     */
    Integer getUniqueVisitorCountByDate(@Param("date") LocalDate date);

    /**
     * 查询每日独立访客统计
     */
    List<AnalysisDataVO.DailyStatItem> getDailyUniqueVisitorStats(@Param("startDate") LocalDate startDate,
                                                                 @Param("endDate") LocalDate endDate);

    /**
     * 查询标签热度（按文章数降序）
     */
    List<AnalysisDataVO.TagRadarItem> getTagRadar(@Param("limit") Integer limit);
}
