package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.common.Result;
import com.lsstop.constant.NotificationConst;
import com.lsstop.domain.dto.NotificationQueryDTO;
import com.lsstop.domain.vo.NotificationDetailVO;
import com.lsstop.domain.vo.NotificationPageVO;
import com.lsstop.enums.NotificationCategoryEnum;
import com.lsstop.enums.NotificationLevelEnum;
import com.lsstop.enums.StatusEnum;
import com.lsstop.exception.BusinessException;
import com.lsstop.service.NotificationService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台通知查询及阅读状态控制层。
 *
 * @author lishusheng
 * @date 2026/09/19
 */
@RestController
public class NotificationController {

    @Resource
    private NotificationService notificationService;

    /**
     * 分页查询通知列表。
     *
     * <p>通知中心不传阅读状态即可查询全部通知；右上角通知弹层传 {@code readStatus=0}，
     * 返回记录为最近未读通知，分页总数可直接用于数字角标。</p>
     *
     * @param query 分页及筛选条件
     * @return 通知分页结果
     */
    @GetMapping("/admin/notification/list")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<NotificationPageVO> listNotifications(NotificationQueryDTO query) {
        validateListParams(query);

        NotificationPageVO page = new NotificationPageVO(
                notificationService.listNotifications(query),
                query.getCurrent(),
                query.getSize(),
                notificationService.countNotifications(query)
        );
        return Result.success(page);
    }

    /**
     * 查询通知详情，包含已脱敏的扩展诊断数据。
     *
     * @param notificationNo 通知编号
     * @return 通知详情
     */
    @GetMapping("/admin/notification/detail/{notificationNo}")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<NotificationDetailVO> getNotificationDetail(@PathVariable String notificationNo) {
        return Result.success(notificationService.getNotificationDetail(notificationNo));
    }

    /**
     * 将指定通知标记为已读。
     *
     * @param notificationNo 通知编号
     * @return 操作结果
     */
    @PutMapping("/admin/notification/read/{notificationNo}")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<Void> markAsRead(@PathVariable String notificationNo) {
        notificationService.markAsRead(notificationNo);
        return Result.success();
    }

    /**
     * 将全部未读通知标记为已读。
     *
     * @return 操作结果
     */
    @PutMapping("/admin/notification/read-all")
    @AccessLimit(seconds = 60, maxCount = 20)
    public Result<Void> markAllAsRead() {
        notificationService.markAllAsRead();
        return Result.success();
    }

    /**
     * 校验通知列表筛选参数，防止无效枚举和超大分页请求进入数据库。
     */
    private void validateListParams(NotificationQueryDTO query) {
        if (query.getCurrent() == null || query.getCurrent() < 1
                || query.getSize() == null || query.getSize() < 1
                || query.getSize() > NotificationConst.MAX_PAGE_SIZE) {
            throw new BusinessException(StatusEnum.PARAM_ERROR, NotificationConst.INVALID_PAGE_PARAM);
        }
        if (query.getCategory() != null && NotificationCategoryEnum.of(query.getCategory()) == null) {
            throw new BusinessException(StatusEnum.PARAM_ERROR, NotificationConst.INVALID_CATEGORY);
        }
        if (query.getLevel() != null && NotificationLevelEnum.of(query.getLevel()) == null) {
            throw new BusinessException(StatusEnum.PARAM_ERROR, NotificationConst.INVALID_LEVEL);
        }
        if (query.getReadStatus() != null
                && query.getReadStatus() != NotificationConst.READ_STATUS_UNREAD
                && query.getReadStatus() != NotificationConst.READ_STATUS_READ) {
            throw new BusinessException(StatusEnum.PARAM_ERROR, NotificationConst.INVALID_READ_STATUS);
        }
        if (query.getStartTime() != null && query.getEndTime() != null
                && query.getStartTime().isAfter(query.getEndTime())) {
            throw new BusinessException(StatusEnum.PARAM_ERROR, NotificationConst.INVALID_TIME_RANGE);
        }
    }
}
