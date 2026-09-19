package com.lsstop.service;

import com.lsstop.domain.dto.NotificationQueryDTO;
import com.lsstop.enums.NotificationEventTypeEnum;
import com.lsstop.enums.NotificationLevelEnum;
import com.lsstop.enums.NotificationSourceTypeEnum;
import com.lsstop.domain.vo.NotificationDetailVO;
import com.lsstop.domain.vo.NotificationListVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

/**
 * 系统通知及异常告警记录入口。
 *
 * <p>调用方只需提供当前业务边界能够获取到的上下文，服务会在调用线程中完成必要的数据快照、
 * 脱敏和去重键计算，再将通知提交到专用线程池异步入库。记录失败只会降级为日志，不能影响
 * 原请求、定时任务或消息消费流程。</p>
 *
 * @author lishusheng
 * @date 2026/09/19
 */
public interface NotificationService {

    /**
     * 记录 HTTP 请求处理过程中抛出的系统异常。
     *
     * @param request 当前 HTTP 请求，用于提取路由、参数、用户、IP 和请求编号
     * @param status HTTP 响应状态码
     * @param throwable 请求处理过程中抛出的异常
     * @return {@code true} 表示通知已成功提交异步队列，{@code false} 表示构建或提交失败；
     *         返回成功不代表数据库事务已经完成
     */
    boolean recordHttpException(HttpServletRequest request, int status, Throwable throwable);

    /**
     * 记录没有捕获到异常对象、但最终返回 5xx 状态的 HTTP 请求。
     *
     * <p>用于兜底覆盖响应状态已被业务代码设置为 5xx、异常却未进入统一异常处理器的场景。</p>
     *
     * @param request 当前 HTTP 请求
     * @param response 当前 HTTP 响应
     * @param costTime 请求耗时，单位为毫秒
     * @return {@code true} 表示通知已成功提交异步队列，否则返回 {@code false}
     */
    boolean recordHttpStatusError(HttpServletRequest request, HttpServletResponse response, long costTime);

    /**
     * 记录超过系统阈值的慢请求。
     *
     * @param request 当前 HTTP 请求
     * @param response 当前 HTTP 响应
     * @param costTime 请求耗时，单位为毫秒
     * @return {@code true} 表示通知已成功提交异步队列，否则返回 {@code false}
     */
    boolean recordSlowRequest(HttpServletRequest request, HttpServletResponse response, long costTime);

    /**
     * 记录定时任务、消息消费、WebSocket 或外部依赖等非 HTTP 边界故障。
     *
     * @param eventType 事件类型，用于分类和同类事件聚合
     * @param sourceType 事件来源类型
     * @param level 通知级别
     * @param title 通知标题
     * @param sourceId 来源追踪编号，可以为空；为空时优先使用当前请求编号
     * @param throwable 原始异常，可以为空；为空时按未知系统异常记录
     * @param details 业务补充信息，会与异常信息一起脱敏后写入扩展字段
     * @return {@code true} 表示通知已成功提交异步队列，否则返回 {@code false}
     */
    boolean recordFailure(NotificationEventTypeEnum eventType,
                          NotificationSourceTypeEnum sourceType,
                          NotificationLevelEnum level,
                          String title,
                          String sourceId,
                          Throwable throwable,
                          Map<String, ?> details);

    /**
     * 分页查询通知摘要列表。
     *
     * <p>右上角通知弹层通过传入 {@code readStatus=0} 复用该查询，返回总数即未读数量。</p>
     *
     * @param query 分页及筛选条件
     * @return 通知摘要列表
     */
    List<NotificationListVO> listNotifications(NotificationQueryDTO query);

    /**
     * 统计符合筛选条件的通知数量。
     *
     * @param query 筛选条件
     * @return 通知数量
     */
    Integer countNotifications(NotificationQueryDTO query);

    /**
     * 查询通知详情。
     *
     * @param notificationNo 通知编号
     * @return 通知详情
     */
    NotificationDetailVO getNotificationDetail(String notificationNo);

    /**
     * 将指定通知标记为已读；重复调用保持幂等。
     *
     * @param notificationNo 通知编号
     */
    void markAsRead(String notificationNo);

    /**
     * 将全部未读通知标记为已读。
     */
    void markAllAsRead();
}
