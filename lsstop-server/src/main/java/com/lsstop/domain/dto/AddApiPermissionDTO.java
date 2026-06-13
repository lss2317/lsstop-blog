package com.lsstop.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增接口权限请求参数
 * <p>当 requestMethod 和 requestUrl 均为 null 时，视为创建目录节点；
 * 两者均非 null 时，视为创建接口节点
 *
 * @author lishusheng
 * @date 2026/06/13
 */
@Data
public class AddApiPermissionDTO {

    /**
     * 父级权限ID（null或0=顶级）
     */
    private Integer parentId;

    /**
     * 权限描述（目录节点和接口节点均必填）
     */
    @Size(max = 50, message = "权限描述不能超过50个字符")
    private String description;

    /**
     * 请求方法（目录节点为null）：GET、POST、PUT、DELETE
     */
    private String requestMethod;

    /**
     * 接口路径（目录节点为null），如 /admin/article/list
     */
    @Size(max = 200, message = "接口路径不能超过200个字符")
    private String requestUrl;

    /**
     * 排序，值越小越靠前
     */
    @Min(value = 1, message = "排序值必须≥1")
    @Max(value = 9999, message = "排序值不能超过9999")
    private Integer sort;

    /**
     * 是否启用：0-禁用 1-启用
     */
    private Integer isEnabled;

}
