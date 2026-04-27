package com.lsstop.domain.dto;

import com.lsstop.domain.BaseData;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 评论请求DTO
 *
 * @author lishusheng
 * @date 2026/01/28
 */
@Data
public class CommentDTO implements BaseData {

    /**
     * 评论目标id
     */
    @NotNull(message = "评论目标不能为空")
    private Integer targetId;

    /**
     * 评论目标类型 1文章 2友链 3说说
     */
    @NotNull(message = "评论目标类型不能为空")
    @Min(value = 1, message = "评论目标类型错误")
    @Max(value = 3, message = "评论目标类型错误")
    private Integer targetType;

    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 1000, message = "评论内容不能超过1000个字符")
    private String content;

    /**
     * 父评论id（回复评论时使用）
     */
    private Integer parentId;

    /**
     * 被回复用户id（回复评论时使用）
     */
    private String replyUserId;
}
