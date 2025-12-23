package com.lsstop.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
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
    @NotEmpty(message = "留言头像不能为空")
    private String avatar;

    /**
     * 留言内容
     */
    @NotEmpty(message = "留言内容不能为空")
    @Size(max = 50, message = "留言内容不能超过50个字符")
    private String messageContent;
}
