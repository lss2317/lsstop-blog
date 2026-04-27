package com.lsstop.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作类型枚举
 *
 * @author lishusheng
 * @date 2026/03/29
 */
@Getter
@AllArgsConstructor
public enum OperationTypeEnum {

    ADD("新增"),
    UPDATE("修改"),
    DELETE("删除");

    /**
     * 描述
     */
    private final String desc;
}
