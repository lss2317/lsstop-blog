package com.lsstop.domain.vo;

import lombok.Data;

/**
 * 留言实体类vo
 *
 * @author lishusheng
 * @date 2025/12/21
 */
@Data
public class MessageVo {

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

}
