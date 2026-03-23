package com.lsstop.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 第三方账号绑定请求参数
 *
 * @author lishusheng
 * @date 2026/03/23
 */
@Data
public class BindCodeDTO {

    /** 授权码 */
    @NotBlank(message = "code不能为空")
    private String code;

}
