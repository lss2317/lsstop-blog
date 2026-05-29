package com.lsstop.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 认证日志分页VO
 *
 * @author lishusheng
 * @date 2026/05/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginLogPageVO {

    /**
     * 记录列表
     */
    private List<LoginLogVO> records;

    /**
     * 当前页码
     */
    private Integer current;

    /**
     * 每页条数
     */
    private Integer size;

    /**
     * 总条数
     */
    private Integer total;
}
