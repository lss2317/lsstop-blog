package com.lsstop.mapper;

import com.lsstop.domain.entity.PageInfo;

import java.util.List;

/**
 * 页面信息数据访问层
 *
 * @author lishusheng
 * @date 2025/12/24
 */
public interface PageInfoMapper {

    /**
     * 获取所有页面列表
     *
     * @return 页面信息列表
     */
    List<PageInfo> listAllPageInfo();
}
