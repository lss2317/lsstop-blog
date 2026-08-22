package com.lsstop.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 更新网站配置请求参数
 *
 * @author lishusheng
 * @date 2026/08/22
 */
@Data
public class UpdateWebsiteConfigDTO {

    /**
     * 配置ID
     */
    @NotNull(message = "配置ID不能为空")
    @Min(value = 1, message = "配置ID不正确")
    private Integer id;

    /**
     * 博主头像（非必填，未传时保留原头像）
     */
    @Size(max = 255, message = "博主头像地址不能超过255个字符")
    private String siteAvatar;

    /**
     * 博客名称
     */
    @NotBlank(message = "博客名称不能为空")
    @Size(max = 50, message = "博客名称不能超过50个字符")
    private String siteName;

    /**
     * 博主名称
     */
    @NotBlank(message = "博主名称不能为空")
    @Size(max = 50, message = "博主名称不能超过50个字符")
    private String siteAuthor;

    /**
     * 博客简介
     */
    @NotBlank(message = "博客简介不能为空")
    @Size(max = 500, message = "博客简介不能超过500个字符")
    private String siteIntro;

    /**
     * 关于我（Markdown原文）
     */
    @NotBlank(message = "关于我不能为空")
    @Size(max = 5000, message = "关于我不能超过5000个字符")
    private String about;

    /**
     * 博客创建时间
     */
    @NotNull(message = "博客创建时间不能为空")
    @PastOrPresent(message = "博客创建时间不能晚于当前时间")
    private LocalDateTime siteStartTime;

    /**
     * QQ链接
     */
    @Size(max = 255, message = "QQ链接不能超过255个字符")
    private String qqUrl;

    /**
     * GitHub链接
     */
    @Size(max = 255, message = "GitHub链接不能超过255个字符")
    private String githubUrl;

    /**
     * Gitee链接
     */
    @Size(max = 255, message = "Gitee链接不能超过255个字符")
    private String giteeUrl;

    /**
     * 用户默认头像
     */
    @NotBlank(message = "用户默认头像不能为空")
    @Size(max = 255, message = "用户默认头像地址不能超过255个字符")
    private String defaultUserAvatar;

    /**
     * 用户注册时默认分配的角色ID
     */
    @NotNull(message = "用户默认角色不能为空")
    @Min(value = 1, message = "用户默认角色不正确")
    private Integer registerDefaultRoleId;

    /**
     * 评论审核（1：是，0：否）
     */
    @NotNull(message = "评论审核设置不能为空")
    @Min(value = 0, message = "评论审核设置不正确")
    @Max(value = 1, message = "评论审核设置不正确")
    private Integer enableCommentReview;

    /**
     * 留言审核（1：是，0：否）
     */
    @NotNull(message = "留言审核设置不能为空")
    @Min(value = 0, message = "留言审核设置不正确")
    @Max(value = 1, message = "留言审核设置不正确")
    private Integer enableMessageReview;

    /**
     * WebSocket基础地址
     */
    @Size(max = 255, message = "WebSocket地址不能超过255个字符")
    private String websocketUrl;

    /**
     * 评论通知（1：是，0：否）
     */
    @NotNull(message = "评论通知设置不能为空")
    @Min(value = 0, message = "评论通知设置不正确")
    @Max(value = 1, message = "评论通知设置不正确")
    private Integer enableCommentEmailNotice;

    /**
     * 评论审核通知（1：是，0：否）
     */
    @NotNull(message = "评论审核通知设置不能为空")
    @Min(value = 0, message = "评论审核通知设置不正确")
    @Max(value = 1, message = "评论审核通知设置不正确")
    private Integer enableCommentReviewNotice;

    /**
     * 留言审核通知（1：是，0：否）
     */
    @NotNull(message = "留言审核通知设置不能为空")
    @Min(value = 0, message = "留言审核通知设置不正确")
    @Max(value = 1, message = "留言审核通知设置不正确")
    private Integer enableMessageReviewNotice;

    /**
     * 评论命中敏感词处理（0：拦截，1：转审核，2：替换发布）
     */
    @NotNull(message = "评论敏感词策略不能为空")
    @Min(value = 0, message = "评论敏感词策略不正确")
    @Max(value = 2, message = "评论敏感词策略不正确")
    private Integer commentIllegalPolicy;

    /**
     * 留言命中敏感词处理（0：拦截，1：转审核，2：替换发布）
     */
    @NotNull(message = "留言敏感词策略不能为空")
    @Min(value = 0, message = "留言敏感词策略不正确")
    @Max(value = 2, message = "留言敏感词策略不正确")
    private Integer messageIllegalPolicy;
}
