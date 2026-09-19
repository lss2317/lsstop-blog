package com.lsstop.service.impl;

import com.lsstop.domain.entity.NotificationEntity;
import com.lsstop.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 通知独立事务持久化服务。
 *
 * <p>该职责必须放在独立 Bean 中，使 {@link Propagation#REQUIRES_NEW} 能够经过 Spring 事务代理生效。
 * 通知写入因此拥有独立事务边界，不会与触发告警的业务事务相互影响。</p>
 */
@Service
@RequiredArgsConstructor
public class NotificationPersistenceService {

    /** 通知数据访问组件 */
    private final NotificationMapper notificationMapper;

    /**
     * 新增通知，或聚合同一时间窗口内已经存在的同类通知。
     *
     * <p>具体新增/聚合逻辑由 Mapper 的原子 upsert 完成，避免并发告警先查询再更新产生竞态。</p>
     *
     * @param notification 已完成脱敏、截断和去重键计算的通知快照
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void upsert(NotificationEntity notification) {
        notificationMapper.upsert(notification);
    }
}
