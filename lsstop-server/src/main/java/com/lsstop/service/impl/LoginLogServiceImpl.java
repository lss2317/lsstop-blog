package com.lsstop.service.impl;

import com.lsstop.constant.CommonConst;
import com.lsstop.constant.LoginLogConst;
import com.lsstop.constant.RabbitMQConst;
import com.lsstop.domain.entity.LoginLogEntity;
import com.lsstop.domain.vo.LoginLogVO;
import com.lsstop.enums.AuthActionEnum;
import com.lsstop.enums.LoginResultEnum;
import com.lsstop.enums.LoginSourceEnum;
import com.lsstop.enums.LoginTypeEnum;
import com.lsstop.mapper.LoginLogMapper;
import com.lsstop.service.LoginLogService;
import com.lsstop.utils.IpUtils;
import com.lsstop.utils.UserAgentUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * 登录日志服务实现类
 *
 * @author lishusheng
 * @date 2026/01/17
 */
@Slf4j
@Service
public class LoginLogServiceImpl implements LoginLogService {

    @Resource
    private LoginLogMapper loginLogMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 插入登录日志
     *
     * @param loginLog 登录日志实体
     */
    @Override
    public void insert(LoginLogEntity loginLog) {
        loginLogMapper.insert(loginLog);
    }

    /**
     * 发送认证日志到MQ
     *
     * @param userId          用户ID
     * @param loginType       登录方式
     * @param source          操作来源
     * @param state           操作结果
     * @param actionType      操作类型（1登录 2退出 3注册）
     * @param loginIdentifier 操作标识（邮箱/openId/uid）
     * @param message         操作信息
     */
    @Override
    public void sendLoginLog(String userId, Integer loginType, Integer source, Integer state, Integer actionType, String loginIdentifier, String message) {
        try {
            HttpServletRequest request = getRequest();
            String ipAddress = request != null ? IpUtils.getIpAddress(request) : CommonConst.UNKNOWN;
            String browser = request != null ? UserAgentUtils.getBrowser(request) : CommonConst.UNKNOWN;
            String os = request != null ? UserAgentUtils.getOS(request) : CommonConst.UNKNOWN;

            LoginLogEntity loginLog = LoginLogEntity.builder()
                    .logNumber(UUID.randomUUID().toString().replace("-", ""))
                    .userId(userId)
                    .loginType(loginType)
                    .loginTime(LocalDateTime.now())
                    .ipAddress(ipAddress)
                    .ipRegion(IpUtils.getIpLocation(ipAddress))
                    .browser(browser)
                    .os(os)
                    .type(source)
                    .state(state)
                    .actionType(actionType)
                    .loginIdentifier(loginIdentifier)
                    .message(message)
                    .build();

            rabbitTemplate.convertAndSend(RabbitMQConst.BLOG_EXCHANGE, RabbitMQConst.LOGIN_LOG_ROUTING_KEY, loginLog);
        } catch (Exception e) {
            log.error("发送认证日志到MQ失败, loginIdentifier={}, message={}", loginIdentifier, message, e);
        }
    }

    /**
     * 分页查询认证日志列表
     *
     * @param current    当前页码
     * @param pageSize   每页数量
     * @param userId     用户ID
     * @param actionType 操作类型：1-登录 2-退出 3-注册
     * @param state      操作结果：0-成功 1-失败
     * @param type       操作来源：0-前台 1-后台 2-非法
     * @param loginType  登录方式：1-邮箱 2-QQ 3-微博
     * @param noUserId   仅筛无用户ID的记录
     * @return 认证日志列表
     */
    @Override
    public List<LoginLogVO> listLoginLogs(Integer current, Integer pageSize,
                                          String userId, Integer actionType,
                                          Integer state, Integer type,
                                          Integer loginType, Boolean noUserId) {
        int offset = (current - 1) * pageSize;
        return loginLogMapper.selectList(offset, pageSize, userId, actionType, state, type, loginType, noUserId);
    }

    /**
     * 统计认证日志总数
     *
     * @param userId     用户ID
     * @param actionType 操作类型：1-登录 2-退出 3-注册
     * @param state      操作结果：0-成功 1-失败
     * @param type       操作来源：0-前台 1-后台 2-非法
     * @param loginType  登录方式：1-邮箱 2-QQ 3-微博
     * @param noUserId   仅筛无用户ID的记录
     * @return 认证日志总数
     */
    @Override
    public Integer countLoginLogTotal(String userId, Integer actionType,
                                      Integer state, Integer type,
                                      Integer loginType, Boolean noUserId) {
        return loginLogMapper.countTotal(userId, actionType, state, type, loginType, noUserId);
    }

    /**
     * 删除认证日志（支持单个和批量删除）
     *
     * @param logNumbers 日志编号列表
     */
    @Override
    public void deleteLoginLogs(List<String> logNumbers) {
        loginLogMapper.deleteByLogNumbers(logNumbers, System.currentTimeMillis());
    }

    /**
     * 导出认证日志为 Excel
     *
     * @param userId     用户ID
     * @param actionType 操作类型：1-登录 2-退出 3-注册
     * @param state      操作结果：0-成功 1-失败
     * @param type       操作来源：0-前台 1-后台 2-非法
     * @param loginType  登录方式：1-邮箱 2-QQ 3-微博
     * @param noUserId   仅筛无用户ID的记录
     * @param response   HTTP响应
     */
    @Override
    public void exportLoginLogs(String userId, Integer actionType, Integer state, Integer type,
                                Integer loginType, Boolean noUserId,
                                HttpServletResponse response) throws IOException {
        List<LoginLogVO> logList = loginLogMapper.selectAllForExport(userId, actionType, state, type, loginType, noUserId);

        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100);
             OutputStream os = response.getOutputStream()) {
            Sheet sheet = workbook.createSheet(LoginLogConst.EXPORT_SHEET_NAME);

            // 写入表头
            String[] headers = LoginLogConst.EXPORT_HEADERS;
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(LoginLogConst.EXPORT_DATE_FORMAT);
            for (int i = 0; i < logList.size(); i++) {
                LoginLogVO log = logList.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(log.getLogNumber());
                row.createCell(1).setCellValue(log.getUserId() != null ? log.getUserId() : "");
                row.createCell(2).setCellValue(log.getNickname() != null ? log.getNickname() : "");
                row.createCell(3).setCellValue(LoginTypeEnum.getByCode(log.getLoginType()) != null
                        ? LoginTypeEnum.getByCode(log.getLoginType()).getDesc() : "-");
                row.createCell(4).setCellValue(log.getLoginTime() != null ? log.getLoginTime().format(formatter) : "");
                row.createCell(5).setCellValue(log.getIpAddress() != null ? log.getIpAddress() : "");
                row.createCell(6).setCellValue(log.getIpRegion() != null ? log.getIpRegion() : "");
                row.createCell(7).setCellValue(log.getBrowser() != null ? log.getBrowser() : "");
                row.createCell(8).setCellValue(log.getOs() != null ? log.getOs() : "");
                row.createCell(9).setCellValue(LoginSourceEnum.getByCode(log.getType()) != null
                        ? LoginSourceEnum.getByCode(log.getType()).getDesc() : "");
                row.createCell(10).setCellValue(LoginResultEnum.getByCode(log.getState()) != null
                        ? LoginResultEnum.getByCode(log.getState()).getDesc() : "");
                row.createCell(11).setCellValue(AuthActionEnum.getByCode(log.getActionType()) != null
                        ? AuthActionEnum.getByCode(log.getActionType()).getDesc() : "");
                row.createCell(12).setCellValue(log.getLoginIdentifier() != null ? log.getLoginIdentifier() : "-");
                row.createCell(13).setCellValue(log.getMessage() != null ? log.getMessage() : "");
            }

            // 设置列宽
            int columnWidth = LoginLogConst.EXPORT_COLUMN_WIDTH * 256;
            for (int i = 0; i < headers.length; i++) {
                sheet.setColumnWidth(i, columnWidth);
            }

            workbook.write(os);
        }
    }

    /**
     * 获取当前请求对象
     */
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

}
