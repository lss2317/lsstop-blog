package com.lsstop.domain.entity;

import com.lsstop.domain.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 通用点赞表实体类
 *
 * @author lishusheng
 * @date 2026/01/01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeRecord implements BaseData {

    /**
     * 点赞id
     */
    private Integer id;

    /**
     * 点赞用户id
     */
    private Integer userId;

    /**
     * 被点赞对象id（说说id/文章id/评论id）
     */
    private Integer targetId;

    /**
     * 点赞类型 1说说 2文章 3评论
     */
    private Integer type;

    /**
     * 状态 1点赞 0取消
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

}
