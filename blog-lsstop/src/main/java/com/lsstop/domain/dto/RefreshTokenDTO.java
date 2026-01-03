package com.lsstop.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 刷新Token请求DTO
 *
 * @author lishusheng
 * @date 2026/01/03
 */
@Data
public class RefreshTokenDTO {

    /**
     * 刷新令牌，用于获取新的访问令牌
     */
    @NotBlank(message = "refreshToken不能为空")
    private String refreshToken;

}
