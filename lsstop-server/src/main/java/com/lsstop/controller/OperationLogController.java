package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.annotation.OperationLog;
import com.lsstop.common.Result;
import com.lsstop.constant.CommentConst;
import com.lsstop.domain.dto.DeleteOperationLogDTO;
import com.lsstop.domain.vo.OperationLogPageVO;
import com.lsstop.enums.OperationModuleEnum;
import com.lsstop.enums.OperationTypeEnum;
import com.lsstop.service.OperationLogService;
import com.lsstop.enums.StatusEnum;
import com.lsstop.exception.BusinessException;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作日志控制层
 *
 * @author lishusheng
 * @date 2026/05/24
 */
@RestController
public class OperationLogController {

    @Resource
    private OperationLogService operationLogService;

    /**
     * 获取操作日志列表（分页）
     *
     * @param current       当前页码
     * @param size          每页条数
     * @param module        操作模块（模糊搜索）
     * @param operationType 操作类型
     * @param userId        用户ID
     * @return 操作日志列表及总数
     */
    @GetMapping("/admin/operation-log/list")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<OperationLogPageVO> listOperationLog(@RequestParam Integer current,
                                                        @RequestParam Integer size,
                                                        @RequestParam(required = false) String module,
                                                        @RequestParam(required = false) String operationType,
                                                        @RequestParam(required = false) String userId) {
        if (current < 1) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), CommentConst.INVALID_PAGE_PARAM);
        }
        if (size < 1) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), CommentConst.INVALID_PAGE_PARAM);
        }
        OperationLogPageVO pageVO = new OperationLogPageVO(
                operationLogService.listOperationLogs(current, size, module, operationType, userId),
                current, size, operationLogService.countTotal(module, operationType, userId)
        );
        return Result.success(pageVO);
    }

    /**
     * 删除操作日志（支持单个和批量删除）
     *
     * @param dto 删除请求参数，包含日志编号列表
     * @return 删除结果
     */
    @PostMapping("/admin/operation-log/delete")
    @AccessLimit(seconds = 60, maxCount = 60)
    @OperationLog(module = OperationModuleEnum.OPERATION_LOG, type = OperationTypeEnum.DELETE, description = "删除操作日志")
    public Result<Void> deleteOperationLog(@RequestBody @Validated DeleteOperationLogDTO dto) {
        operationLogService.deleteByLogNumbers(dto.getLogNumbers());
        return Result.success();
    }
}
