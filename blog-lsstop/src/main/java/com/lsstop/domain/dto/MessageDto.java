package com.lsstop.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * 留言实体类dto
 *
 * @author lishusheng
 * @date 2025/12/22
 */
@Data
public class MessageDto {

    /**
     * 昵称
     */
    @NotEmpty(message = "留言昵称不能为空")
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 留言内容
     */
    private String messageContent;
}
