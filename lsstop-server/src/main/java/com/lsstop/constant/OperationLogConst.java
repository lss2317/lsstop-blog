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

    // ==================== 导出 Excel 相关常量 ====================

    /**
     * 导出 Excel 表头
     */
    public static final String[] EXPORT_HEADERS = {
            "日志编号", "系统模块", "操作类型", "操作描述", "请求路径",
            "用户ID", "操作人员", "IP地址", "IP归属地", "浏览器",
            "操作系统", "状态", "消耗时间", "操作时间", "错误信息",
            "请求参数", "返回参数"
    };

    /**
     * 导出日期格式
     */
    public static final String EXPORT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 导出 Sheet 名称
     */
    public static final String EXPORT_SHEET_NAME = "操作日志";

    /**
     * 状态成功文本
     */
    public static final String STATE_SUCCESS_TEXT = "成功";

    /**
     * 状态失败文本
     */
    public static final String STATE_FAIL_TEXT = "失败";

    /**
     * 耗时单位
     */
    public static final String COST_TIME_UNIT = "ms";

    /**
     * 导出列宽（字符单位估算）
     */
    public static final int EXPORT_COLUMN_WIDTH = 20;

    /**
     * 导出文件名前缀
     */
    public static final String EXPORT_FILENAME_PREFIX = "操作日志_";

    /**
     * 导出文件扩展名
     */
    public static final String EXPORT_FILE_EXTENSION = ".xlsx";

}
