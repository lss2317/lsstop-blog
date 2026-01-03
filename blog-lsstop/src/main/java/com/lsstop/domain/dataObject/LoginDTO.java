package com.lsstop.domain.dataObject;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 用户登录请求参数
 *
 * @author lishusheng
 * @date 2026/01/03
 */
@Data
public class LoginDTO {

    /**
     * 登陆方式标识符
     */
    @NotBlank(message = "邮箱不能为空")
    private String identifier;

    /**
     * 登录凭证
     */
    @NotBlank(message = "密码不能为空")
    private String credential;

    /**
     * 登录方式：1邮箱密码 2QQ 3微博
     */
    @NotNull(message = "登录方式不能为空")
    @Min(value = 1, message = "登录方式错误")
    @Max(value = 3, message = "登录方式错误")
    private Integer loginType;

}
