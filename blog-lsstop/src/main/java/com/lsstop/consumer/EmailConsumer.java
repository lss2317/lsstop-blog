package com.lsstop.consumer;

import com.lsstop.constant.RabbitMQConst;
import com.lsstop.domain.dto.EmailDTO;
import com.lsstop.service.EmailService;
import com.rabbitmq.client.Channel;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 邮件消费者
 *
 * @author lss
 * @date 2026/2/14
 */
@Slf4j
@Component
public class EmailConsumer {

    @Resource
    private JavaMailSender mailSender;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private EmailService emailService;

    @Value("${spring.mail.username}")
    private String from;

    /**
     * 消费邮件消息
     *
     * @param emailDTO    邮件DTO
     * @param message     消息对象
     * @param channel     通道
     * @param deliveryTag 消息标签
     */
    @RabbitListener(queues = RabbitMQConst.EMAIL_QUEUE)
    public void handleEmail(EmailDTO emailDTO, Message message, Channel channel,
                            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            sendEmail(emailDTO);
            channel.basicAck(deliveryTag, false);
            log.info("邮件发送成功: {} - {}", emailDTO.getType().getDesc(), emailDTO.getTo());
        } catch (Exception e) {
            handleRetry(emailDTO, message, channel, deliveryTag, e);
        }
    }

    /**
     * 发送邮件
     */
    private void sendEmail(EmailDTO emailDTO) throws MessagingException {
        String subject = emailService.generateSubject(emailDTO);
        String content = emailService.generateContent(emailDTO);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setFrom(from);
        helper.setTo(emailDTO.getTo());
        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(mimeMessage);
    }

    /**
     * 处理重试逻辑
     */
    private void handleRetry(EmailDTO emailDTO, Message message, Channel channel,
                             long deliveryTag, Exception e) {
        MessageProperties properties = message.getMessageProperties();
        Integer retryCount = (Integer) properties.getHeaders().getOrDefault(RabbitMQConst.RETRY_COUNT_HEADER, 0);

        try {
            channel.basicNack(deliveryTag, false, false);

            if (retryCount < RabbitMQConst.MAX_RETRY) {
                int nextRetryCount = retryCount + 1;
                log.warn("邮件发送失败，第{}次重试: {}", nextRetryCount, e.getMessage());
                rabbitTemplate.convertAndSend(RabbitMQConst.BLOG_EXCHANGE, RabbitMQConst.EMAIL_ROUTING_KEY,
                        emailDTO, msg -> {
                            msg.getMessageProperties().setHeader(RabbitMQConst.RETRY_COUNT_HEADER, nextRetryCount);
                            return msg;
                        });
            } else {
                log.error("邮件发送失败，已达最大重试次数，收件人: {}，错误: {}", emailDTO.getTo(), e.getMessage());
            }
        } catch (IOException ex) {
            log.error("消息处理失败: {}", ex.getMessage());
        }
    }
}
