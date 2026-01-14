package com.lsstop.domain.entity;

import com.lsstop.domain.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户基础实体
 *
 * @author lishusheng
 * @date 2026/01/01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements BaseData {

    /**
     * 用户id
     */
    private Integer id;

    /**
     * 用户唯一标识
     */
    private String userUid;

    /**
     * 状态 1正常 0禁用
     */
    private Integer status;

    /**
     * 是否删除 0否 1是
     */
    private Integer isDelete;

    /**
     * 最近一次成功登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
