package com.lsstop.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 修改密码请求参数
 *
 * @author lishusheng
 * @date 2026/03/16
 */
@Data
public class ChangePasswordDTO {

    /**
     * 旧密码
     */
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    private String newPassword;

}
