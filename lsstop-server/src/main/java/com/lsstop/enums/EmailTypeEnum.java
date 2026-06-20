package com.lsstop.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 邮件类型枚举
 *
 * @author lss
 * @date 2026/2/14
 */
@Getter
@AllArgsConstructor
public enum EmailTypeEnum {

    /**
     * 验证码
     */
    VERIFY_CODE("验证码", "【%s】您的验证码", "email/verify-code"),

    /**
     * 评论通知
     */
    COMMENT_NOTICE("评论通知", "【%s】您收到了新的评论", "email/comment-notice"),

    /**
     * 回复通知
     */
    REPLY_NOTICE("回复通知", "【%s】您的评论收到了回复", "email/reply-notice"),

    /**
     * 评论审核通过通知
     */
    REVIEW_PASS("审核通过", "【%s】您的评论已通过审核", "email/review-pass"),

    /**
     * 留言审核通过通知
     */
    MESSAGE_REVIEW_PASS("留言审核通过", "【%s】您的留言已通过审核", "email/message-review-pass"),

    /**
     * 账号开通通知
     */
    WELCOME("账号开通通知", "【%s】您的账号已开通", "email/welcome");

    /**
     * 描述
     */
    private final String desc;

    /**
     * 邮件主题模板
     */
    private final String subjectTemplate;

    /**
     * 模板路径
     */
    private final String templatePath;
}
