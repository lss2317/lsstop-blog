package com.lsstop.domain.entity;

import com.lsstop.domain.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 说说实体
 *
 * @author lishusheng
 * @date 2025/12/30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TalkEntity implements BaseData {

    /**
     * id
     */
    private Integer id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 说说内容
     */
    private String content;

    /**
     * 是否置顶 1.是 0.否
     */
    private Integer isTop;

    /**
     * 状态 1.公开 2.私密
     */
    private Integer status;

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
