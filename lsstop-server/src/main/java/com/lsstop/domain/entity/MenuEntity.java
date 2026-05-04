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
     * 路由path
     */
    private String path;

    /**
     * 组件路径（如 system/user/index）
     */
    private String component;

    /**
     * 重定向路径
     */
    private String redirect;

    /**
     * 菜单标题
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
     * 是否在菜单中隐藏
     */
    private Integer isHide;

    /**
     * 是否在标签页中隐藏
     */
    private Integer isHideTab;

    /**
     * 是否缓存页面
     */
    private Integer keepAlive;

    /**
     * 是否全屏页面
     */
    private Integer isFullPage;

    /**
     * 是否一级菜单
     */
    private Integer isFirstLevel;

    /**
     * 是否固定标签页
     */
    private Integer fixedTab;

    /**
     * 外部链接URL
     */
    private String link;

    /**
     * 是否iframe嵌入
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
