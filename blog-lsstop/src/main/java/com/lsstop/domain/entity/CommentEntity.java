package com.lsstop.domain.entity;

import com.lsstop.domain.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评论实体
 *
 * @author lishusheng
 * @date 2026/01/02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentEntity implements BaseData {

    /**
     * id
     */
    private Integer id;

    /**
     * 评论用户id
     */
    private String userId;

    /**
     * 评论目标id
     */
    private Integer targetId;

    /**
     * 评论目标类型 1文章 2友链 3说说
     */
    private Integer targetType;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 父评论id
     */
    private Integer parentId;

    /**
     * 被回复用户id
     */
    private String replyUserId;

    /**
     * IP所在地
     */
    private String ipRegion;

    /**
     * 审核状态(0:正常 1:待审核)
     */
    private Integer review;

    /**
     * 删除时间戳，0表示未删除
     */
    private Long deletedAt;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
