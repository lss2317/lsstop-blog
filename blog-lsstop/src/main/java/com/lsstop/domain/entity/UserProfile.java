package com.lsstop.domain.entity;

import com.lsstop.domain.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户资料实体类
 *
 * @author lishusheng
 * @date 2026/01/01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile implements BaseData {

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 个人网站
     */
    private String website;

    /**
     * 个人简介
     */
    private String intro;

    /**
     * 是否删除
     */
    private Integer isDelete;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
