package com.lsstop.mapper;

import com.lsstop.domain.entity.NotificationEntity;

/**
 * 系统通知及异常告警数据访问层
 *
 * @author lishusheng
 * @date 2026/09/19
 */
public interface NotificationMapper {

    /**
     * 新增通知；同一聚合窗口内的重复事件累加发生次数
     *
     * @param notification 通知实体
     */
    void upsert(NotificationEntity notification);
}
