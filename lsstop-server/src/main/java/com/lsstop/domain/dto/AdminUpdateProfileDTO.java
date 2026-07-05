package com.lsstop.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 后台个人中心更新资料请求参数
 *
 * @author lishusheng
 * @date 2026/07/05
 */
@Data
public class AdminUpdateProfileDTO {

    /**
     * 昵称
     */
    @NotBlank(message = "昵称不能为空")
    @Size(min = 1, max = 20, message = "昵称长度需要在1-20个字符之间")
    private String nickname;

    /**
     * 个人网站
     */
    @Size(max = 200, message = "个人网站长度不能超过200个字符")
    private String website;

    /**
     * 个人简介
     */
    @Size(max = 100, message = "个人简介长度不能超过100个字符")
    private String intro;

}
