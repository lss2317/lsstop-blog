package com.lsstop.service;

import com.lsstop.domain.dto.EmailDTO;
import com.lsstop.domain.entity.CommentEntity;
import com.lsstop.domain.entity.UserProfileEntity;

/**
 * 邮件服务
 *
 * @author lss
 * @date 2026/2/14
 */
public interface EmailService {

    /**
     * 生成邮件主题
     *
     * @param emailDTO 邮件DTO
     * @return 邮件主题
     */
    String generateSubject(EmailDTO emailDTO);

    /**
     * 生成邮件内容
     *
     * @param emailDTO 邮件DTO
     * @return 邮件内容（HTML格式）
     */
    String generateContent(EmailDTO emailDTO);

    /**
     * 发送评论通知
     *
     * @param comment     评论实体
     * @param userProfile 评论用户资料
     */
    void sendCommentNotice(CommentEntity comment, UserProfileEntity userProfile);

    /**
     * 发送评论审核通过通知
     *
     * @param comment 评论实体
     */
    void sendCommentReviewPassNotice(CommentEntity comment);

    /**
     * 发送留言审核通过通知
     *
     * @param email   收件邮箱
     * @param content 留言内容
     */
    void sendMessageReviewPassNotice(String email, String content);
}
