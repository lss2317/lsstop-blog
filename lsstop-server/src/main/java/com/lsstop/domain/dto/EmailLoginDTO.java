package com.lsstop.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 邮箱密码登录请求参数
 *
 * @author lishusheng
 * @date 2026/01/04
 */
@Data
public class EmailLoginDTO {

    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

}
