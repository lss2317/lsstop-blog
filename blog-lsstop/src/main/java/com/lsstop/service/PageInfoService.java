package com.lsstop.service;

import com.lsstop.domain.entity.PageInfo;

import java.util.List;

/**
 * 页面信息服务接口
 *
 * @author lishusheng
 * @date 2025/12/24
 */
public interface PageInfoService {

    /**
     * 获取页面列表
     *
     * @return 页面信息列表
     */
    List<PageInfo> listAllPageInfo();
}
