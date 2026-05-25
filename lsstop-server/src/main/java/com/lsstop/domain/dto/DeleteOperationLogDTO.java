package com.lsstop.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 删除操作日志请求参数
 *
 * @author lishusheng
 * @date 2026/05/25
 */
@Data
public class DeleteOperationLogDTO {

    /**
     * 日志编号列表
     */
    @NotEmpty(message = "日志编号列表不能为空")
    private List<String> logNumbers;
}
