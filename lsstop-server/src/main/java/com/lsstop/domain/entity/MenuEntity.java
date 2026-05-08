package com.lsstop.domain.entity;

import com.lsstop.domain.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 菜单路由权限实体
 *
 * @author lishusheng
 * @date 2026/05/04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuEntity implements BaseData {

    /**
     * 菜单ID
     */
    private Integer id;

    /**
     * 父级ID（0=顶级）
     */
    private Integer parentId;

    /**
     * 类型：1-目录 2-菜单 3-按钮
     */
    private Integer menuType;

    /**
     * 路由name（唯一标识）
     */
    private String name;

    /**
     * 路由path（相对路径，如 console；前端会自动拼接父路径为绝对路径）
     */
    private String path;

    /**
     * 组件路径（menuType=2时必填，如 /home/console；menuType=1时必须为NULL）
     */
    private String component;

    /**
     * 重定向路径（目录类型可留NULL，前端会自动推导首个子路由）
     */
    private String redirect;

    /**
     * 菜单标题（对应前端 meta.title）
     */
    private String title;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 排序，值越小越靠前
     */
    private Integer sort;

    /**
     * 是否在菜单中隐藏：0-显示 1-隐藏
     */
    private Integer isHide;

    /**
     * 是否在标签页中隐藏：0-显示 1-隐藏
     */
    private Integer isHideTab;

    /**
     * 是否缓存页面（KeepAlive）：0-不缓存 1-缓存
     */
    private Integer keepAlive;

    /**
     * 是否全屏页面（不显示侧边栏和顶栏）：0-否 1-是
     */
    private Integer isFullPage;

    /**
     * 是否一级菜单（无父目录，直接显示在顶部导航，不展开侧边栏）：0-否 1-是
     */
    private Integer isFirstLevel;

    /**
     * 是否固定标签页（不可被用户关闭）：0-否 1-是
     */
    private Integer fixedTab;

    /**
     * 外部链接URL（配合isIframe：isIframe=0新窗口打开，isIframe=1页面内嵌入）
     */
    private String link;

    /**
     * 是否iframe嵌入：0-新窗口打开外链 1-iframe内嵌展示
     */
    private Integer isIframe;

    /**
     * 激活菜单路径（隐藏菜单用）
     */
    private String activePath;

    /**
     * 权限标识（如 article:add）
     */
    private String authMark;

    /**
     * 是否启用：0-禁用 1-启用
     */
    private Integer isEnabled;

    /**
     * 删除时间戳，0表示未删除
     */
    private Long deletedAt;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
