package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.annotation.OperationLog;
import com.lsstop.common.Result;
import com.lsstop.constant.CommentConst;
import com.lsstop.constant.LoginLogConst;
import com.lsstop.domain.dto.DeleteLoginLogDTO;
import com.lsstop.domain.vo.LoginLogPageVO;
import com.lsstop.enums.OperationModuleEnum;
import com.lsstop.enums.OperationTypeEnum;
import com.lsstop.enums.StatusEnum;
import com.lsstop.exception.BusinessException;
import com.lsstop.service.LoginLogService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

/**
 * 认证日志控制层
 *
 * @author lishusheng
 * @date 2026/05/29
 */
@RestController
public class LoginLogController {

    @Resource
    private LoginLogService loginLogService;

    /**
     * 获取认证日志列表（分页）
     *
     * @param current    当前页码
     * @param size       每页条数
     * @param userId     用户ID
     * @param actionType 操作类型：1-登录 2-退出 3-注册
     * @param state      操作结果：0-成功 1-失败
     * @param type       操作来源：0-前台 1-后台 2-非法
     * @param loginType  登录方式：1-邮箱 2-QQ 3-微博
     * @param noUserId   仅筛无用户ID的记录
     * @return 认证日志列表及总数
     */
    @GetMapping("/admin/login-log/list")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<LoginLogPageVO> listLoginLog(@RequestParam Integer current,
                                                @RequestParam Integer size,
                                                @RequestParam(required = false) String userId,
                                                @RequestParam(required = false) Integer actionType,
                                                @RequestParam(required = false) Integer state,
                                                @RequestParam(required = false) Integer type,
                                                @RequestParam(required = false) Integer loginType,
                                                @RequestParam(required = false) Boolean noUserId) {
        if (current < 1) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), CommentConst.INVALID_PAGE_PARAM);
        }
        if (size < 1) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), CommentConst.INVALID_PAGE_PARAM);
        }
        LoginLogPageVO pageVO = new LoginLogPageVO(
                loginLogService.listLoginLogs(current, size, userId, actionType, state, type, loginType, noUserId),
                current, size, loginLogService.countLoginLogTotal(userId, actionType, state, type, loginType, noUserId)
        );
        return Result.success(pageVO);
    }

    /**
     * 删除认证日志（支持单个和批量删除）
     *
     * @param dto 删除请求参数，包含日志编号列表
     * @return 删除结果
     */
    @PostMapping("/admin/login-log/delete")
    @AccessLimit(seconds = 60, maxCount = 60)
    @OperationLog(module = OperationModuleEnum.LOGIN_LOG, type = OperationTypeEnum.DELETE, description = "删除认证日志")
    public Result<Void> deleteLoginLog(@RequestBody @Validated DeleteLoginLogDTO dto) {
        loginLogService.deleteLoginLogs(dto.getLogNumbers());
        return Result.success();
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
    @GetMapping("/admin/login-log/export")
    @AccessLimit(seconds = 60, maxCount = 10)
    public void exportLoginLog(@RequestParam(required = false) String userId,
                               @RequestParam(required = false) Integer actionType,
                               @RequestParam(required = false) Integer state,
                               @RequestParam(required = false) Integer type,
                               @RequestParam(required = false) Integer loginType,
                               @RequestParam(required = false) Boolean noUserId,
                               HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String filename = URLEncoder.encode(
                LoginLogConst.EXPORT_FILENAME_PREFIX + LocalDate.now(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment;filename*=UTF-8''" + filename + LoginLogConst.EXPORT_FILE_EXTENSION);
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        response.setHeader(HttpHeaders.PRAGMA, "no-cache");
        response.setDateHeader(HttpHeaders.EXPIRES, 0);
        loginLogService.exportLoginLogs(userId, actionType, state, type, loginType, noUserId, response);
    }
}
