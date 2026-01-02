package com.lsstop.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 点赞请求DTO
 *
 * @author lishusheng
 * @date 2026/01/02
 */
@Data
public class LikeDTO {

    /**
     * 用户id
     */
    @NotBlank(message = "用户id不能为空")
    private String userId;

    /**
     * 目标id（说说id/文章id/评论id）
     */
    @NotNull(message = "目标id不能为空")
    private Integer targetId;

    /**
     * 点赞类型（1说说 2文章 3评论）
     */
    @NotNull(message = "点赞类型不能为空")
    @Min(value = 1, message = "点赞类型错误")
    @Max(value = 3, message = "点赞类型错误")
    private Integer type;

}
