package com.lsstop.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 网站配置信息实体类
 *
 * @author lishusheng
 * @date 2025/12/23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("blog_website_config")
public class WebsiteConfig {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 网站头像
     */
    private String websiteAvatar;

    /**
     * 网站名称
     */
    private String websiteName;

    /**
     * 网站作者
     */
    private String websiteAuthor;

    /**
     * 网站介绍
     */
    private String websiteIntro;

    /**
     * 网站公告
     */
    private String websiteNotice;

    /**
     * 网站创建时间
     */
    private LocalDateTime websiteCreateTime;

    /**
     * 网站备案号
     */
    private String websiteRecordNo;

    /**
     * qq
     */
    private String qq;

    /**
     * github
     */
    private String github;

    /**
     * gitee
     */
    private String gitee;

    /**
     * 游客头像
     */
    private String touristAvatar;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 留言审核(1:是、0:否)
     */
    private Integer commentReview;

    /**
     * 是否邮箱通知
     */
    private Integer emailNotice;

    /**
     * 是否打赏
     */
    private Integer reward;

    /**
     * 微信二维码
     */
    private String weixinQrcode;

    /**
     * 支付宝二维码
     */
    @TableField("alipayQrcode")
    private String alipayQrcode;

    /**
     * 是否开启聊天室
     */
    private Integer chatRoom;

    /**
     * 是否开启音乐
     */
    private Integer musicPlayer;

    /**
     * websocket地址
     */
    private String websocketUrl;

    /**
     * 关于我信息
     */
    private String about;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
