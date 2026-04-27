package com.lsstop.config;

import com.lsstop.constant.RabbitMQConst;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置类
 *
 * @author lss
 * @date 2026/1/17
 */
@Configuration
public class RabbitMQConfig {

    /**
     * 消息转换器（使用 JSON 序列化）
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitTemplate 配置
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    /**
     * 博客主题交换机
     */
    @Bean
    public TopicExchange blogExchange() {
        return new TopicExchange(RabbitMQConst.BLOG_EXCHANGE, true, false);
    }


    /**
     * 邮件队列
     */
    @Bean
    public Queue emailQueue() {
        return new Queue(RabbitMQConst.EMAIL_QUEUE, true);
    }

    /**
     * 邮件队列绑定交换机
     */
    @Bean
    public Binding emailBinding(Queue emailQueue, TopicExchange blogExchange) {
        return BindingBuilder.bind(emailQueue).to(blogExchange).with(RabbitMQConst.EMAIL_ROUTING_KEY);
    }

    /**
     * 操作日志队列
     */
    @Bean
    public Queue operationLogQueue() {
        return new Queue(RabbitMQConst.OPERATION_LOG_QUEUE, true);
    }

    /**
     * 操作日志队列绑定交换机
     */
    @Bean
    public Binding operationLogBinding(Queue operationLogQueue, TopicExchange blogExchange) {
        return BindingBuilder.bind(operationLogQueue).to(blogExchange).with(RabbitMQConst.OPERATION_LOG_ROUTING_KEY);
    }

    /**
     * 登录日志队列
     */
    @Bean
    public Queue loginLogQueue() {
        return new Queue(RabbitMQConst.LOGIN_LOG_QUEUE, true);
    }

    /**
     * 登录日志队列绑定交换机
     */
    @Bean
    public Binding loginLogBinding(Queue loginLogQueue, TopicExchange blogExchange) {
        return BindingBuilder.bind(loginLogQueue).to(blogExchange).with(RabbitMQConst.LOGIN_LOG_ROUTING_KEY);
    }
}
