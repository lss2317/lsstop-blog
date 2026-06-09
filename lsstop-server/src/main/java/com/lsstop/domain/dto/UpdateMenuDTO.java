package com.lsstop.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 编辑菜单请求参数
 *
 * @author lishusheng
 * @date 2026/06/08
 */
@Data
public class UpdateMenuDTO {

    /**
     * 菜单ID
     */
    @NotNull(message = "菜单ID不能为空")
    private Integer id;

    /**
     * 父级ID（0=顶级）
     */
    private Integer parentId;

    /**
     * 菜单类型：directory-目录 menu-菜单 button-按钮 iframe-内嵌 link-外链
     */
    @NotBlank(message = "菜单类型不能为空")
    private String menuType;

    /**
     * 路由name（唯一标识）
     */
    @Size(max = 50, message = "路由标识不能超过50个字符")
    private String name;

    /**
     * 路由path
     */
    @Size(max = 100, message = "路由地址不能超过100个字符")
    private String path;

    /**
     * 组件路径
     */
    @Size(max = 200, message = "组件路径不能超过200个字符")
    private String component;

    /**
     * 菜单标题
     */
    @NotBlank(message = "菜单名称不能为空")
    @Size(min = 2, max = 20, message = "菜单名称长度为2~20个字符")
    private String title;

    /**
     * 菜单图标
     */
    @Size(max = 100, message = "图标名称不能超过100个字符")
    private String icon;

    /**
     * 排序
     */
    @Min(value = 1, message = "排序值必须≥1")
    @Max(value = 9999, message = "排序值不能超过9999")
    private Integer sort;

    /**
     * 是否在菜单中隐藏
     */
    private Boolean isHide;

    /**
     * 是否在标签页中隐藏
     */
    private Boolean isHideTab;

    /**
     * 是否缓存页面
     */
    private Boolean keepAlive;

    /**
     * 是否全屏页面
     */
    private Boolean isFullPage;

    /**
     * 是否固定标签页
     */
    private Boolean fixedTab;

    /**
     * 外部链接URL
     */
    @Size(max = 500, message = "外部链接不能超过500个字符")
    private String link;

    /**
     * 激活菜单路径（隐藏菜单用）
     */
    @Size(max = 100, message = "激活路径不能超过100个字符")
    private String activePath;

    /**
     * 权限标识
     */
    @Size(max = 30, message = "权限标识不能超过30个字符")
    private String authMark;

    /**
     * 是否启用
     */
    private Boolean isEnabled;

}
