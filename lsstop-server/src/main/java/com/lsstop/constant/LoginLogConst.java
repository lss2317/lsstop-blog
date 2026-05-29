package com.lsstop.constant;

/**
 * 认证日志常量
 *
 * @author lishusheng
 * @date 2026/05/29
 */
public class LoginLogConst {

    // ==================== 导出 Excel 相关常量 ====================

    /**
     * 导出 Excel 表头
     */
    public static final String[] EXPORT_HEADERS = {
            "日志编号", "用户ID", "用户昵称", "登录方式", "操作时间",
            "登录IP", "IP所在地", "浏览器", "操作系统", "操作来源",
            "操作结果", "操作类型", "操作标识", "操作信息"
    };

    /**
     * 导出日期格式
     */
    public static final String EXPORT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 导出 Sheet 名称
     */
    public static final String EXPORT_SHEET_NAME = "认证日志";

    /**
     * 导出列宽（字符单位估算）
     */
    public static final int EXPORT_COLUMN_WIDTH = 20;

    /**
     * 导出文件名前缀
     */
    public static final String EXPORT_FILENAME_PREFIX = "认证日志_";

    /**
     * 导出文件扩展名
     */
    public static final String EXPORT_FILE_EXTENSION = ".xlsx";
}
