package com.lsstop.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 新增公告请求参数
 *
 * @author lishusheng
 * @date 2026/08/29
 */
@Data
public class AddAnnouncementDTO {

    /**
     * 公告标题
     */
    @NotBlank(message = "公告标题不能为空")
    @Size(max = 100, message = "公告标题不能超过100个字符")
    private String title;

    /**
     * Markdown 格式的公告内容
     */
    @NotBlank(message = "公告内容不能为空")
    private String content;

    /**
     * 公告类型：1-弹窗公告 2-首页展示 3-全部展示
     */
    @NotNull(message = "公告类型不能为空")
    @Min(value = 1, message = "公告类型不正确")
    @Max(value = 3, message = "公告类型不正确")
    private Integer type;

    /**
     * 显示优先级，值越大越靠前
     */
    @NotNull(message = "优先级不能为空")
    @Min(value = 0, message = "优先级不能小于0")
    @Max(value = 999, message = "优先级不能超过999")
    private Integer priority;

    /**
     * 是否启用：0-禁用 1-启用
     */
    @NotNull(message = "启用状态不能为空")
    @Min(value = 0, message = "启用状态不正确")
    @Max(value = 1, message = "启用状态不正确")
    private Integer isEnabled;

    /**
     * 生效开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd['T'][' ']HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 生效结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd['T'][' ']HH:mm:ss")
    private LocalDateTime endTime;
}
