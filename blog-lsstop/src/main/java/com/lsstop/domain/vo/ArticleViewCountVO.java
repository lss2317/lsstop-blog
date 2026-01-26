package com.lsstop.domain.vo;

import com.lsstop.domain.BaseData;
import lombok.Data;

/**
 * 文章访问量统计VO
 *
 * @author lishusheng
 * @date 2026/01/26
 */
@Data
public class ArticleViewCountVO implements BaseData {

    /**
     * 文章ID
     */
    private Integer id;

    /**
     * 访问量
     */
    private Integer viewCount;

}
