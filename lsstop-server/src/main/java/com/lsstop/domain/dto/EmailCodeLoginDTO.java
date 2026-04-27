package com.lsstop.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 邮箱验证码登录请求参数
 *
 * @author lishusheng
 * @date 2026/02/22
 */
@Data
public class EmailCodeLoginDTO {

    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String code;

}
