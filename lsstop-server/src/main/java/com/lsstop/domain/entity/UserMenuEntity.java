package com.lsstop.domain.entity;

import com.lsstop.domain.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户菜单权限调整实体
 *
 * @author lishusheng
 * @date 2026/05/04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMenuEntity implements BaseData {

    /**
     * id
     */
    private Integer id;

    /**
     * 用户uid（关联 blog_user.user_uid）
     */
    private String userId;

    /**
     * 菜单ID（关联 blog_menu.id）
     */
    private Integer menuId;

    /**
     * 1-额外授予 2-额外排除
     */
    private Integer type;

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
