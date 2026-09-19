package com.lsstop.config;

import com.lsstop.constant.NotificationConst;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 系统通知异步入库线程池配置
 *
 * <p>使用独立、有界队列，队列满时直接拒绝并由调用方降级记录日志，
 * 禁止使用CallerRunsPolicy，避免通知任务反向占用主请求线程。</p>
 */
@Configuration
public class NotificationAsyncConfig {

    /**
     * 创建通知异步入库专用线程池。
     *
     * <p>该线程池与业务线程池隔离，并使用有界队列限制待处理通知数量；当线程池和队列均已满时，
     * {@link ThreadPoolExecutor.AbortPolicy} 会立即拒绝新任务，由通知服务捕获拒绝异常并降级为日志，
     * 从而避免通知入库反向阻塞或拖慢主请求线程。应用关闭时会在限定时间内等待已提交任务完成。</p>
     *
     * @return 配置完成的通知异步入库线程池
     */
    @Bean(name = NotificationConst.TASK_EXECUTOR_BEAN_NAME)
    public ThreadPoolTaskExecutor notificationTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 核心线程常驻处理通知；积压超过队列容量后，最多扩容到最大线程数。
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(2);

        // 使用有界队列，防止异常洪峰导致通知任务无限堆积并耗尽内存。
        executor.setQueueCapacity(1000);

        // 非核心线程空闲达到该时间后回收，避免低流量时长期占用资源。
        executor.setKeepAliveSeconds(60);

        // 设置独立线程名前缀，便于日志检索和线程问题排查。
        executor.setThreadNamePrefix("notification-writer-");

        // 优雅停机：等待队列中已提交的通知任务完成，但等待时间不超过配置上限。
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(10);

        // 队列满时直接拒绝，禁止回退到调用线程执行，确保通知入库不影响主业务流程。
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        return executor;
    }
}
