package com.lsstop.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 修改邮箱请求参数
 *
 * @author lishusheng
 * @date 2026/03/14
 */
@Data
public class ChangeEmailDTO {

    /**
     * 新邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String newEmail;

    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String code;

}
