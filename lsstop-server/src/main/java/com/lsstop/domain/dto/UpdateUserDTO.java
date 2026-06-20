package com.lsstop.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 后台更新用户请求参数
 *
 * @author lishusheng
 * @date 2026/06/20
 */
@Data
public class UpdateUserDTO {

    /**
     * 用户ID
     */
    @NotBlank(message = "用户ID不能为空")
    private String userId;

    /**
     * 昵称
     */
    @NotBlank(message = "昵称不能为空")
    @Size(min = 1, max = 20, message = "昵称长度需要在1-20个字符之间")
    private String nickname;

    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 头像
     */
    private String avatar;

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

    /**
     * 状态（0-禁用 1-正常）
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 角色ID列表
     */
    @NotEmpty(message = "角色不能为空")
    private List<Integer> roleIds;
}
