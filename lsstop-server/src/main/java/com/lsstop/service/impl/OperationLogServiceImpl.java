package com.lsstop.service.impl;

import com.lsstop.domain.entity.OperationLogEntity;
import com.lsstop.domain.vo.OperationLogVO;
import com.lsstop.mapper.OperationLogMapper;
import com.lsstop.service.OperationLogService;
import com.lsstop.constant.OperationLogConst;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 操作日志服务实现类
 *
 * @author lishusheng
 * @date 2026/03/29
 */
@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Resource
    private OperationLogMapper operationLogMapper;

    /**
     * 插入操作日志
     *
     * @param operationLog 操作日志实体
     */
    @Override
    public void insert(OperationLogEntity operationLog) {
        operationLogMapper.insert(operationLog);
    }

    /**
     * 分页查询操作日志列表
     *
     * @param current       当前页码
     * @param pageSize      每页数量
     * @param module        操作模块（模糊搜索）
     * @param operationType 操作类型
     * @param userId        用户ID
     * @return 操作日志列表
     */
    @Override
    public List<OperationLogVO> listOperationLogs(Integer current, Integer pageSize,
                                                  String module, String operationType, String userId) {
        int offset = (current - 1) * pageSize;
        return operationLogMapper.selectList(offset, pageSize, module, operationType, userId);
    }

    /**
     * 统计操作日志总数
     *
     * @param module        操作模块（模糊搜索）
     * @param operationType 操作类型
     * @param userId        用户ID
     * @return 操作日志总数
     */
    @Override
    public Integer countTotal(String module, String operationType, String userId) {
        return operationLogMapper.countTotal(module, operationType, userId);
    }

    /**
     * 删除操作日志（支持单个和批量删除）
     *
     * @param logNumbers 日志编号列表
     */
    @Override
    public void deleteByLogNumbers(List<String> logNumbers) {
        operationLogMapper.deleteByLogNumbers(logNumbers, System.currentTimeMillis());
    }

    /**
     * 导出操作日志为 Excel
     *
     * @param module        操作模块（模糊搜索）
     * @param operationType 操作类型
     * @param userId        用户ID
     * @param response      HTTP响应
     */
    @Override
    public void exportOperationLogs(String module, String operationType, String userId,
                                    HttpServletResponse response) throws IOException {
        List<OperationLogVO> logList = operationLogMapper.selectAllForExport(module, operationType, userId);

        // 使用SXSSFWorkbook支持大量数据导出
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100);
             OutputStream os = response.getOutputStream()) {
            Sheet sheet = workbook.createSheet(OperationLogConst.EXPORT_SHEET_NAME);

            // 写入表头
            String[] headers = OperationLogConst.EXPORT_HEADERS;
            Row headerRow = sheet.createRow(0);
            CellStyle headerCellStyle = workbook.createCellStyle();
            Font headerCellFont = workbook.createFont();
            headerCellFont.setBold(true);
            headerCellStyle.setFont(headerCellFont);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerCellStyle);
            }

            // 写入数据行
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(OperationLogConst.EXPORT_DATE_FORMAT);
            for (int i = 0; i < logList.size(); i++) {
                OperationLogVO log = logList.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(log.getLogNumber());
                row.createCell(1).setCellValue(log.getModule());
                row.createCell(2).setCellValue(log.getOperationType());
                row.createCell(3).setCellValue(log.getDescription());
                row.createCell(4).setCellValue(log.getRequestUrl());
                row.createCell(5).setCellValue(log.getUserId());
                row.createCell(6).setCellValue(log.getNickname());
                row.createCell(7).setCellValue(log.getIpAddress());
                row.createCell(8).setCellValue(log.getIpRegion());
                row.createCell(9).setCellValue(log.getBrowser());
                row.createCell(10).setCellValue(log.getOs());
                // state: 0成功 1失败
                row.createCell(11).setCellValue(log.getState() != null && log.getState() == OperationLogConst.STATE_SUCCESS
                                ? OperationLogConst.STATE_SUCCESS_TEXT : OperationLogConst.STATE_FAIL_TEXT);
                row.createCell(12).setCellValue(log.getCostTime() != null ? log.getCostTime() + OperationLogConst.COST_TIME_UNIT : "");
                row.createCell(13).setCellValue(log.getCreateTime() != null ? log.getCreateTime().format(formatter) : "");
                row.createCell(14).setCellValue(log.getErrorMsg() != null ? log.getErrorMsg() : "");
                row.createCell(15).setCellValue(log.getRequestParam() != null ? log.getRequestParam() : "");
                row.createCell(16).setCellValue(log.getResponseParam() != null ? log.getResponseParam() : "");
            }

            // 设置列宽
            int columnWidth = OperationLogConst.EXPORT_COLUMN_WIDTH * 256;
            for (int i = 0; i < headers.length; i++) {
                sheet.setColumnWidth(i, columnWidth);
            }

            workbook.write(os);
        }
    }

}
