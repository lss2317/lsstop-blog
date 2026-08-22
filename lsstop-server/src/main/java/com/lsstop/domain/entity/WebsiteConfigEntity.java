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
     * 用户注册时默认分配的角色ID
     */
    private Integer registerDefaultRoleId;

    /**
     * 评论审核(1:是、0:否)
     */
    private Integer enableCommentReview;

    /**
     * 留言审核(1:是、0:否)
     */
    private Integer enableMessageReview;

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
     * 留言审核通知(1:是、0:否)
     */
    private Integer enableMessageReviewNotice;

    /**
     * 评论命中敏感词处理(0:拦截,1:转审核,2:替换发布)
     */
    private Integer commentIllegalPolicy;

    /**
     * 留言命中敏感词处理(0:拦截,1:转审核,2:替换发布)
     */
    private Integer messageIllegalPolicy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
