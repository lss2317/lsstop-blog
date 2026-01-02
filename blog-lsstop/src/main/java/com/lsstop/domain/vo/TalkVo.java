package com.lsstop.domain.vo;

import com.lsstop.domain.BaseData;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 前台说说视图类
 *
 * @author lishusheng
 * @date 2026/01/01
 */
@Data
public class TalkVO implements BaseData {

    /**
     * id
     */
    private Integer id;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 说说内容
     */
    private String content;

    /**
     * 是否置顶 1.是 0.否
     */
    private Integer isTop;

    /**
     * 点赞量
     */
    private Integer likeCount;

    /**
     * 图片列表
     */
    private List<String> imgList;

    /**
     * 评论量
     */
    private Integer commentCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
