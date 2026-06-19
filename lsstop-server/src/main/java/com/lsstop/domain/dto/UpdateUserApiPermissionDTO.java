package com.lsstop.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 修改用户接口权限请求参数
 *
 * @author lishusheng
 * @date 2026/06/19
 */
@Data
public class UpdateUserApiPermissionDTO {

    /**
     * 用户ID
     */
    @NotBlank(message = "用户ID不能为空")
    private String userId;

    /**
     * 接口权限ID列表（全量选中的接口权限ID）
     * <p>空列表表示清空该用户的所有个性化接口权限
     */
    @NotNull(message = "接口权限ID列表不能为空")
    private List<Integer> apiIds;
}
