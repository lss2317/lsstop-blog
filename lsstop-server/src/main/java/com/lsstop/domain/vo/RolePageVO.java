package com.lsstop.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 角色分页VO
 *
 * @author lishusheng
 * @date 2026/06/02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePageVO {

    /**
     * 记录列表
     */
    private List<RoleVO> records;

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
