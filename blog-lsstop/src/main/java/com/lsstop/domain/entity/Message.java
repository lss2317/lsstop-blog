package com.lsstop.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lsstop.domain.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 留言实体类
 *
 * @author lishusheng
 * @date 2025/12/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("blog_message")
public class Message implements BaseData {

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户ip
     */
    private String ipAddress;

    /**
     * 用户地址
     */
    private String ipSource;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 留言内容
     */
    private String messageContent;

    /**
     * 是否要审核 1、否 0、是
     */
    private Integer review;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
