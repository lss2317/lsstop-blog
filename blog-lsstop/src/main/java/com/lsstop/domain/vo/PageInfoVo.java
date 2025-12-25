package com.lsstop.domain.vo;

import lombok.Data;

/**
 * 页面信息vo
 *
 * @author lishusheng
 * @date 2025/12/24
 */
@Data
public class PageInfoVo {

    /**
     * 页面名称
     */
    private String pageName;

    /**
     * 页面标签
     */
    private String pageLabel;

    /**
     * 页面封面
     */
    private String pageCover;
}
