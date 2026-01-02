package com.lsstop.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 说说统计数据DTO
 * <p>用于Redis与数据库之间同步点赞数和评论数</p>
 *
 * @author lishusheng
 * @date 2026/01/02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TalkStatsDto {

    /**
     * 说说id
     */
    private Integer id;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;
}
