package com.lsstop.domain.entity;

import com.lsstop.domain.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户登录认证与第三方账号绑定实体
 *
 * @author lishusheng
 * @date 2026/01/01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthEntity implements BaseData {

    /**
     * id
     */
    private Integer id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 登录方式：1邮箱密码 2QQ 3微博
     */
    private Integer loginType;

    /**
     * 登录唯一标识（邮箱 / QQ openId / 微博uid）
     */
    private String identifier;

    /**
     * 登录凭证（密码hash / access_token，第三方登录可为空）
     */
    private String credential;

    /**
     * 是否删除：0否 1是
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
