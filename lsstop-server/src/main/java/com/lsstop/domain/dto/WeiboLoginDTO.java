package com.lsstop.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 微博登录请求参数
 *
 * @author lishusheng
 * @date 2026/01/04
 */
@Data
public class WeiboLoginDTO {

    /** 微博授权码 */
    @NotBlank(message = "code不能为空")
    private String code;

}
