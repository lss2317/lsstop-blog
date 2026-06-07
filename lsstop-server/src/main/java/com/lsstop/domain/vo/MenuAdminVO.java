package com.lsstop.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单管理VO（后台管理专用，包含 parentId、sort 等内部字段）
 *
 * @author lishusheng
 * @date 2026/06/07
 */
@Data
public class MenuAdminVO {

    /**
     * 菜单ID
     */
    private Integer id;

    /**
     * 父级ID（0=顶级）
     */
    private Integer parentId;

    /**
     * 类型：1-目录 2-菜单 3-按钮 4-内嵌 5-外链
     */
    private Integer menuType;

    /**
     * 路由name（唯一标识）
     */
    private String name;

    /**
     * 路由path
     */
    private String path;

    /**
     * 组件路径（如 dashboard/console）
     */
    private String component;

    /**
     * 菜单标题
     */
    private String title;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 排序
     */
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
     * 是否一级菜单
     */
    private Boolean isFirstLevel;

    /**
     * 是否固定标签页
     */
    private Boolean fixedTab;

    /**
     * 外部链接URL
     */
    private String link;

    /**
     * 是否iframe嵌入
     */
    private Boolean isIframe;

    /**
     * 激活菜单路径（隐藏菜单用）
     */
    private String activePath;

    /**
     * 权限标识（如 add）
     */
    private String authMark;

    /**
     * 是否启用：0-禁用 1-启用
     */
    private Integer isEnabled;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 子菜单
     */
    private List<MenuAdminVO> children;

}
