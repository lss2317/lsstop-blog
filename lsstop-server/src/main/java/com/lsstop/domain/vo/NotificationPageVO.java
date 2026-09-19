package com.lsstop.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 通知分页结果。
 *
 * @author lishusheng
 * @date 2026/09/19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPageVO {

    /** 通知记录 */
    private List<NotificationListVO> records;

    /** 当前页码 */
    private Integer current;

    /** 每页条数 */
    private Integer size;

    /** 符合当前筛选条件的记录总数 */
    private Integer total;
}
