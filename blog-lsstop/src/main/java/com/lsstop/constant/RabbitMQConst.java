package com.lsstop.constant;

/**
 * RabbitMQ 常量
 *
 * @author lss
 * @date 2026/1/17
 */
public class RabbitMQConst {

    /**
     * 博客交换机
     */
    public static final String BLOG_EXCHANGE = "blog.topic.exchange";

    /**
     * 邮件队列
     */
    public static final String EMAIL_QUEUE = "blog.email.queue";

    /**
     * 邮件路由键
     */
    public static final String EMAIL_ROUTING_KEY = "blog.email";


    /**
     * 操作日志队列
     */
    public static final String OPERATION_LOG_QUEUE = "blog.operation.log.queue";

    /**
     * 操作日志路由键
     */
    public static final String OPERATION_LOG_ROUTING_KEY = "blog.operation.log";


    /**
     * 登录日志队列
     */
    public static final String LOGIN_LOG_QUEUE = "blog.login.log.queue";

    /**
     * 登录日志路由键
     */
    public static final String LOGIN_LOG_ROUTING_KEY = "blog.login.log";

    /**
     * 消息重试次数头
     */
    public static final String RETRY_COUNT_HEADER = "x-retry-count";

    /**
     * 最大重试次数
     */
    public static final int MAX_RETRY = 3;
}
