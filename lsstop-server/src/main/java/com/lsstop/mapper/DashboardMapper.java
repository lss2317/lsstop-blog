package com.lsstop.mapper;

import com.lsstop.domain.vo.ConsoleDataVO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
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
    List<ConsoleDataVO.DailyStatItem> getDailyCommentStats(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

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
}
