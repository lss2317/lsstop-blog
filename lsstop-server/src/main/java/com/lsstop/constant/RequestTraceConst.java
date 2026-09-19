package com.lsstop.constant;

/**
 * 请求链路追踪常量
 *
 * @author lishusheng
 * @date 2026/09/06
 */
public final class RequestTraceConst {

    /**
     * 请求属性及MDC中的请求编号名称
     */
    public static final String REQUEST_ID = "requestId";

    /**
     * 返回给客户端的请求编号响应头
     */
    public static final String REQUEST_ID_HEADER = "X-Request-Id";

    /**
     * 请求异常已经写入通知表的标记，避免过滤器与全局异常处理器重复记录
     */
    public static final String NOTIFICATION_RECORDED = "notificationRecorded";

    private RequestTraceConst() {
    }
}
