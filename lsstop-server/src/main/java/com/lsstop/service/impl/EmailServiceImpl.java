package com.lsstop.service.impl;

import com.lsstop.config.BlogConfig;
import com.lsstop.constant.RabbitMQConst;
import com.lsstop.domain.dto.EmailDTO;
import com.lsstop.domain.entity.CommentEntity;
import com.lsstop.domain.entity.UserProfileEntity;
import com.lsstop.enums.CommentTypeEnum;
import com.lsstop.enums.EmailTypeEnum;
import com.lsstop.mapper.ArticleMapper;
import com.lsstop.mapper.AuthMapper;
import com.lsstop.mapper.CommentMapper;
import com.lsstop.mapper.TalkMapper;
import com.lsstop.service.EmailService;
import com.lsstop.utils.EmojiUtils;
import com.lsstop.utils.StringUtils;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * 邮件服务实现
 *
 * @author lss
 * @date 2026/2/14
 */
@Service
public class EmailServiceImpl implements EmailService {

    @Resource
    private TemplateEngine templateEngine;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private AuthMapper authMapper;

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private TalkMapper talkMapper;

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private BlogConfig blogConfig;

    @Override
    public String generateSubject(EmailDTO emailDTO) {
        // 优先使用自定义主题
        if (emailDTO.getSubject() != null && !emailDTO.getSubject().isBlank()) {
            return emailDTO.getSubject();
        }
        return String.format(emailDTO.getType().getSubjectTemplate(), blogConfig.getName());
    }

    @Override
    public String generateContent(EmailDTO emailDTO) {
        Context context = new Context();
        context.setVariable("blogName", blogConfig.getName());

        Map<String, Object> params = emailDTO.getParams();
        if (params != null) {
            params.forEach(context::setVariable);
        }

        return templateEngine.process(emailDTO.getType().getTemplatePath(), context);
    }

    @Override
    public void sendCommentNotice(CommentEntity comment, UserProfileEntity userProfile) {
        String nickname = userProfile.getNickname();
        String topicType = getTopicType(comment.getTargetType());
        String topic = getTopicTitle(comment.getTargetType(), comment.getTargetId());
        String url = buildCommentUrl(comment);

        if (comment.getParentId() == null) {
            // 新评论：通知博主
            sendEmail(blogConfig.getOwnerEmail(), EmailTypeEnum.COMMENT_NOTICE, Map.of(
                    "topicType", topicType,
                    "topic", topic,
                    "nickname", nickname,
                    "content", EmojiUtils.toHtml(StringUtils.truncate(comment.getContent(), 100)),
                    "url", url
            ));
        } else {
            // 回复评论：通知被回复的用户
            CommentEntity parentComment = commentMapper.selectById(comment.getParentId());
            if (parentComment == null) {
                return;
            }
            // replyUserId为空时（回复主评论），查询主评论作者
            String targetUserId = comment.getReplyUserId();
            String originalContent = EmojiUtils.toHtml(StringUtils.truncate(parentComment.getContent(), 100));
            if (targetUserId == null || targetUserId.isBlank()) {
                targetUserId = parentComment.getUserId();
            }
            String replyUserEmail = authMapper.selectEmailByUserId(targetUserId);
            if (replyUserEmail != null && !replyUserEmail.isBlank()) {
                sendEmail(replyUserEmail, EmailTypeEnum.REPLY_NOTICE, Map.of(
                        "topicType", topicType,
                        "topic", topic,
                        "nickname", nickname,
                        "originalContent", originalContent,
                        "replyContent", EmojiUtils.toHtml(StringUtils.truncate(comment.getContent(), 100)),
                        "url", url
                ));
            }
        }
    }

    @Override
    public void sendCommentReviewPassNotice(CommentEntity comment) {
        String email = authMapper.selectEmailByUserId(comment.getUserId());
        if (email == null || email.isBlank()) {
            return;
        }
        String topicType = getTopicType(comment.getTargetType());
        String topic = getTopicTitle(comment.getTargetType(), comment.getTargetId());
        String url = buildCommentUrl(comment);

        sendEmail(email, EmailTypeEnum.REVIEW_PASS, Map.of(
                "topicType", topicType,
                "topic", topic,
                "content", comment.getContent(),
                "url", url
        ));
    }

    @Override
    public void sendMessageReviewPassNotice(String email, String content) {
        if (email == null || email.isBlank()) {
            return;
        }
        sendEmail(email, EmailTypeEnum.MESSAGE_REVIEW_PASS, Map.of(
                "content", content,
                "url", blogConfig.getUrl() + "/message"
        ));
    }

    /**
     * 获取评论类型名称
     */
    private String getTopicType(Integer targetType) {
        CommentTypeEnum type = CommentTypeEnum.of(targetType);
        if (type == null) {
            return "内容";
        }
        return switch (type) {
            case ARTICLE -> "文章";
            case TALK -> "说说";
            case FRIEND_LINK -> "友链";
        };
    }

    /**
     * 获取评论目标标题
     */
    private String getTopicTitle(Integer targetType, Integer targetId) {
        CommentTypeEnum type = CommentTypeEnum.of(targetType);
        if (type == null) {
            return "";
        }
        return switch (type) {
            case ARTICLE -> {
                String title = articleMapper.selectTitleById(targetId);
                yield title != null ? title : "";
            }
            case TALK -> {
                String content = talkMapper.selectContentById(targetId);
                yield content != null ? StringUtils.truncate(content, 20) : "";
            }
            case FRIEND_LINK -> "友情链接";
        };
    }

    /**
     * 构建评论详情链接
     */
    private String buildCommentUrl(CommentEntity comment) {
        CommentTypeEnum type = CommentTypeEnum.of(comment.getTargetType());
        if (type == null) {
            return blogConfig.getUrl();
        }
        return switch (type) {
            case ARTICLE -> blogConfig.getUrl() + "/article/" + comment.getTargetId();
            case TALK -> blogConfig.getUrl() + "/talk/" + comment.getTargetId();
            case FRIEND_LINK -> blogConfig.getUrl() + "/friendLink";
        };
    }

    /**
     * 发送邮件到队列
     */
    private void sendEmail(String to, EmailTypeEnum type, Map<String, Object> params) {
        EmailDTO emailDTO = EmailDTO.builder()
                .to(to)
                .type(type)
                .params(new HashMap<>(params))
                .build();
        rabbitTemplate.convertAndSend(RabbitMQConst.BLOG_EXCHANGE, RabbitMQConst.EMAIL_ROUTING_KEY, emailDTO);
    }
}
