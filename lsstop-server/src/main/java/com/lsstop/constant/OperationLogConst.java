package com.lsstop.constant;

import java.util.Set;

/**
 * 操作日志常量
 *
 * @author lishusheng
 * @date 2026/03/29
 */
public class OperationLogConst {

    /**
     * 操作成功
     */
    public static final int STATE_SUCCESS = 0;

    /**
     * 操作失败
     */
    public static final int STATE_FAIL = 1;

    /**
     * 错误信息最大长度
     */
    public static final int MAX_ERROR_MSG_LENGTH = 500;

    /**
     * 请求参数最大长度
     */
    public static final int MAX_PARAM_LENGTH = 2000;

    /**
     * 返回参数最大长度
     */
    public static final int MAX_RESPONSE_PARAM_LENGTH = 2000;

    /**
     * 敏感字段
     */
    public static final Set<String> SENSITIVE_FIELDS = Set.of(
            "password", "credential", "token", "secret", "oldPassword", "newPassword"
    );

}
