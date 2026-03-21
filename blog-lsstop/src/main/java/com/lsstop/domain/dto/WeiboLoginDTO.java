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

    /**
     * 微博 OAuth access_token
     */
    @NotBlank(message = "accessToken不能为空")
    private String accessToken;

    /**
     * 微博 uid
     */
    @NotBlank(message = "uid不能为空")
    private String uid;

}
