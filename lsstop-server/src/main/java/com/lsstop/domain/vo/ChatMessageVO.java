package com.lsstop.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 聊天消息VO
 *
 * @author lishusheng
 * @date 2026/04/04
 */
@Data
public class ChatMessageVO {

    /**
     * 消息id
     */
    private Integer id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 图片JSON原始字符串（Mapper映射用，不返回前端）
     */
    @JsonIgnore
    private String imagesJson;

    /**
     * 图片URL列表
     */
    private List<String> images;

    /**
     * IP所在地
     */
    private String ipRegion;

    /**
     * 发送时间
     */
    private LocalDateTime createTime;
}
