package com.lsstop.domain.vo;

import lombok.Data;

/**
 * 新增留言返回VO
 *
 * @author lishusheng
 * @date 2025/12/21
 */
@Data
public class AddMessageVO {

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
     * 审核状态（0-正常 1-待审核）
     */
    private Integer review;

}
