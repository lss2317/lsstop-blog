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
    UPDATE("编辑"),
    DELETE("删除"),
    PUBLISH("发布"),
    REVOKE("撤回"),
    UPLOAD("上传"),
    EXPORT("导出"),
    RESTORE("恢复"),
    CLEAR("清空"),
    AUTHORIZATION("权限变更"),
    RESET("重置");

    /**
     * 描述
     */
    private final String desc;
}
