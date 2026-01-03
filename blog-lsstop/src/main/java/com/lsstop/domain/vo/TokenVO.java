package com.lsstop.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token响应VO
 *
 * @author lishusheng
 * @date 2026/01/03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenVO {

    /**
     * 访问令牌，用于接口鉴权
     */
    private String accessToken;

    /**
     * 刷新令牌，用于获取新的访问令牌
     */
    private String refreshToken;

}
