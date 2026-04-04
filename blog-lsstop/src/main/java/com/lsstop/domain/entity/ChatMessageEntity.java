package com.lsstop.domain.entity;

import com.lsstop.domain.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 聊天消息实体
 *
 * @author lishusheng
 * @date 2026/04/04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageEntity implements BaseData {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 图片URL列表
     */
    private String images;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * IP所在地
     */
    private String ipRegion;

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
