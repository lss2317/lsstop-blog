package com.lsstop.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户注册请求DTO
 *
 * @author lishusheng
 * @date 2026/03/04
 */
@Data
public class RegisterDTO {

    /**
     * 邮箱地址
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String code;
}
