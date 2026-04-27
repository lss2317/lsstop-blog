package com.lsstop.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 发送验证码请求DTO
 *
 * @author lishusheng
 * @date 2026/02/21
 */
@Data
public class SendCodeDTO {

    /**
     * 邮箱地址
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 验证码用途
     * 1-登录 2-注册 3-找回密码
     */
    @NotNull(message = "验证码用途不能为空")
    private Integer purpose;
}
