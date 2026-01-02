package com.lsstop.domain.dataObject;

import com.lsstop.domain.BaseData;
import lombok.Data;

/**
 * 评论统计DO
 *
 * @author lishusheng
 * @date 2026/01/03
 */
@Data
public class CommentCountDO implements BaseData {

    /**
     * 目标id
     */
    private Integer targetId;

    /**
     * 评论数
     */
    private Integer commentCount;

}
