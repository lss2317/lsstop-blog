package com.lsstop.domain.dataObject;

import com.lsstop.domain.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 说说统计DO
 *
 * @author lishusheng
 * @date 2026/01/02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TalkStatsDO implements BaseData {

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
