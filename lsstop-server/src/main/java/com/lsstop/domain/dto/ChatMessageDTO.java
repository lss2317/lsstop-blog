package com.lsstop.domain.dto;

import com.lsstop.domain.BaseData;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 聊天消息DTO
 *
 * @author lishusheng
 * @date 2026/04/04
 */
@Data
public class ChatMessageDTO implements BaseData {

    /**
     * 消息内容
     */
    @Size(max = 500, message = "消息内容不能超过500个字符")
    private String content;

    /**
     * 图片URL列表
     */
    @Size(max = 3, message = "图片数量不能超过3张")
    private List<String> images;
}
