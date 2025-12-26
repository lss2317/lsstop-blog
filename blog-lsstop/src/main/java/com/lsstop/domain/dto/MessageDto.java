package com.lsstop.domain.dto;

import com.lsstop.domain.BaseData;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 留言实体类dto
 *
 * @author lishusheng
 * @date 2025/12/22
 */
@Data
public class MessageDto implements BaseData {

    /**
     * 昵称
     */
    @NotBlank(message = "留言昵称不能为空")
    private String nickname;

    /**
     * 头像
     */
    @NotBlank(message = "留言头像不能为空")
    private String avatar;

    /**
     * 留言内容
     */
    @NotBlank(message = "留言内容不能为空")
    @Size(max = 50, message = "留言内容不能超过50个字符")
    private String messageContent;

    /**
     * 是否要审核(0：否、1：是)
     */
    @NotNull(message = "留言审核参数不能为空")
    @Min(value = 0, message = "留言审核参数错误")
    @Max(value = 1, message = "留言审核参数错误")
    private Integer review;


}
