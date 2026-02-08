package com.lsstop.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 删除评论请求DTO
 *
 * @author lishusheng
 * @date 2026/02/08
 */
@Data
public class DeleteCommentDTO {

    /**
     * 评论ID
     */
    @NotNull(message = "评论ID不能为空")
    @Min(value = 1, message = "评论ID不合法")
    private Integer commentId;
}
