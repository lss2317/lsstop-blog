package com.lsstop.domain.entity;

import com.lsstop.domain.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 网站配置信息实体
 *
 * @author lishusheng
 * @date 2025/12/23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebsiteConfigEntity implements BaseData {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 网站头像
     */
    private String siteAvatar;

    /**
     * 网站名称
     */
    private String siteName;

    /**
     * 网站作者
     */
    private String siteAuthor;

    /**
     * 网站介绍
     */
    private String siteIntro;

    /**
     * 关于我
     */
    private String about;

    /**
     * 网站创建时间
     */
    private LocalDateTime siteStartTime;

    /**
     * QQ链接
     */
    private String qqUrl;

    /**
     * GitHub链接
     */
    private String githubUrl;

    /**
     * Gitee链接
     */
    private String giteeUrl;

    /**
     * 用户默认头像
     */
    private String defaultUserAvatar;

    /**
     * 评论审核(1:是、0:否)
     */
    private Integer enableCommentReview;

    /**
     * 留言审核(1:是、0:否)
     */
    private Integer enableMessageReview;

    /**
     * 是否开启聊天室(1:是、0:否)
     */
    private Integer enableChatRoom;

    /**
     * 是否开启音乐播放器(1:是、0:否)
     */
    private Integer enableMusicPlayer;

    /**
     * websocket地址
     */
    private String websocketUrl;

    /**
     * 评论通知(1:是、0:否)
     */
    private Integer enableCommentEmailNotice;

    /**
     * 评论审核通知(1:是、0:否)
     */
    private Integer enableCommentReviewNotice;

    /**
     * 留言通知(1:是、0:否)
     */
    private Integer enableMessageEmailNotice;

    /**
     * 留言审核通知(1:是、0:否)
     */
    private Integer enableMessageReviewNotice;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
