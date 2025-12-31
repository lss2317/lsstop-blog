package com.lsstop.domain.entity;

import com.lsstop.domain.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 说说实体类
 *
 * @author lishusheng
 * @date 2025/12/30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Talk implements BaseData {

    /**
     * id
     */
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 说说内容
     */
    private String content;

    /**
     * 是否置顶 1.是 0.否
     */
    private Integer isTop;

    /**
     * 状态 1.公开 2.私密
     */
    private Integer status;

    /**
     * 是否删除 1.是 0.否
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
