package com.lsstop.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 评论列表分页VO
 *
 * @author lishusheng
 * @date 2026/02/01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentPageVO {

    /**
     * 评论列表
     */
    private List<CommentVO> list;

    /**
     * 总数
     */
    private Integer total;
}
