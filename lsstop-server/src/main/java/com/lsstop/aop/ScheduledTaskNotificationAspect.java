package com.lsstop.aop;

import com.lsstop.enums.NotificationEventTypeEnum;
import com.lsstop.enums.NotificationLevelEnum;
import com.lsstop.enums.NotificationSourceTypeEnum;
import com.lsstop.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 捕获未被任务自身处理的定时任务异常并写入系统通知表
 */
@Aspect
@Component
@RequiredArgsConstructor
public class ScheduledTaskNotificationAspect {

    /** 系统通知记录服务 */
    private final NotificationService notificationService;

    /**
     * 环绕执行定时任务，并记录任务未自行处理的异常。
     *
     * <p>任务正常执行时原样返回执行结果；异常逃逸时，将任务类名、方法名和异常信息异步写入
     * 系统通知表，随后继续抛出原始异常，确保不改变 Spring 定时任务原有的异常处理语义。</p>
     *
     * @param joinPoint 当前定时任务方法的连接点
     * @return 定时任务方法的原始返回值
     * @throws Throwable 定时任务方法抛出的原始异常
     */
    @Around("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            // 使用“类名#方法名”作为稳定的任务来源标识，便于聚合和排查同类异常。
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String taskName = signature.getDeclaringType().getSimpleName() + "#" + signature.getName();
            notificationService.recordFailure(
                    NotificationEventTypeEnum.TASK_FAILURE,
                    NotificationSourceTypeEnum.TASK,
                    NotificationLevelEnum.ERROR,
                    "定时任务执行失败：" + taskName,
                    taskName,
                    throwable,
                    Map.of("taskName", taskName)
            );
            // 记录通知不能吞掉任务异常，否则会改变调度框架对任务失败的判断和日志行为。
            throw throwable;
        }
    }
}
