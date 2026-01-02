package com.lsstop.domain.vo;

import com.lsstop.domain.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 用户点赞信息视图类
 *
 * @author lishusheng
 * @date 2026/01/03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLikeVO implements BaseData {

    /**
     * 用户点赞的说说id列表
     */
    private Set<Integer> talkLikeIds;

    /**
     * 用户点赞的文章id列表
     */
    private Set<Integer> articleLikeIds;

    /**
     * 用户点赞的评论id列表
     */
    private Set<Integer> commentLikeIds;

}
