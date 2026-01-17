package com.lsstop.consumer;

import com.lsstop.constant.RabbitMQConst;
import com.lsstop.domain.entity.LoginLogEntity;
import com.lsstop.service.LoginLogService;
import com.rabbitmq.client.Channel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 登录日志消费者
 *
 * @author lishusheng
 * @date 2026/01/17
 */
@Slf4j
@Component
public class LoginLogConsumer {

    @Resource
    private LoginLogService loginLogService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 消费登录日志消息
     *
     * @param loginLog    登录日志实体
     * @param message     消息对象
     * @param channel     通道
     * @param deliveryTag 消息标签
     */
    @RabbitListener(queues = RabbitMQConst.LOGIN_LOG_QUEUE)
    public void handleLoginLog(LoginLogEntity loginLog, Message message, Channel channel,
                               @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            loginLogService.insert(loginLog);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            handleRetry(loginLog, message, channel, deliveryTag, e);
        }
    }

    /**
     * 处理重试逻辑
     */
    private void handleRetry(LoginLogEntity loginLog, Message message, Channel channel,
                             long deliveryTag, Exception e) {
        MessageProperties properties = message.getMessageProperties();
        Integer retryCount = (Integer) properties.getHeaders().getOrDefault(RabbitMQConst.RETRY_COUNT_HEADER, 0);

        try {
            // 拒绝原消息
            channel.basicNack(deliveryTag, false, false);

            if (retryCount < RabbitMQConst.MAX_RETRY) {
                // 未达最大重试次数，重新发送消息
                int nextRetryCount = retryCount + 1;
                log.warn("登录日志消费失败，第{}次重试: {}", nextRetryCount, e.getMessage());
                rabbitTemplate.convertAndSend(RabbitMQConst.BLOG_EXCHANGE, RabbitMQConst.LOGIN_LOG_ROUTING_KEY,
                        loginLog, msg -> {
                            msg.getMessageProperties().setHeader(RabbitMQConst.RETRY_COUNT_HEADER, nextRetryCount);
                            return msg;
                        });
            } else {
                // 达到最大重试次数
                log.error("登录日志消费失败，已达最大重试次数: {}", e.getMessage());
            }
        } catch (IOException ex) {
            log.error("消息处理失败: {}", ex.getMessage());
        }
    }

}
