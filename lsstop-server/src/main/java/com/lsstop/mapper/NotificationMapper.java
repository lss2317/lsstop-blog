package com.lsstop.mapper;

import com.lsstop.domain.dto.NotificationQueryDTO;
import com.lsstop.domain.entity.NotificationEntity;
import com.lsstop.domain.vo.NotificationDetailVO;
import com.lsstop.domain.vo.NotificationListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    /**
     * 分页查询通知摘要列表。
     *
     * @param query 分页及筛选条件
     * @return 通知摘要列表
     */
    List<NotificationListVO> selectList(NotificationQueryDTO query);

    /**
     * 统计符合筛选条件的通知数量。
     *
     * @param query 筛选条件
     * @return 通知数量
     */
    Integer countTotal(NotificationQueryDTO query);

    /**
     * 根据通知编号查询详情。
     *
     * @param notificationNo 通知编号
     * @return 通知详情，不存在时返回 {@code null}
     */
    NotificationDetailVO selectDetailByNotificationNo(@Param("notificationNo") String notificationNo);

    /**
     * 将单条通知标记为已读。
     *
     * @param notificationNo 通知编号
     * @return 实际更新条数
     */
    int markAsRead(@Param("notificationNo") String notificationNo);

    /**
     * 将全部未读通知标记为已读。
     *
     * @return 实际更新条数
     */
    int markAllAsRead();
}
