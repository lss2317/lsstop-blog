package com.lsstop.domain.entity;

import com.lsstop.domain.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 说说图片实体
 *
 * @author lishusheng
 * @date 2025/12/30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TalkImageEntity implements BaseData {

    /**
     * 图片id
     */
    private Integer id;

    /**
     * 说说id
     */
    private Integer talkId;

    /**
     * 图片地址
     */
    private String imageUrl;

    /**
     * 图片顺序
     */
    private Integer sort;

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
