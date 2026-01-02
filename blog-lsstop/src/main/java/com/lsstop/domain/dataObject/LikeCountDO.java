package com.lsstop.domain.dataObject;

import com.lsstop.domain.BaseData;
import lombok.Data;

/**
 * 点赞统计DO
 *
 * @author lishusheng
 * @date 2026/01/03
 */
@Data
public class LikeCountDO implements BaseData {

    /**
     * 目标id
     */
    private Integer targetId;

    /**
     * 点赞数
     */
    private Integer likeCount;

}
