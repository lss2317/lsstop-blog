package com.lsstop.service.impl;

import com.lsstop.domain.entity.PageInfo;
import com.lsstop.mapper.PageInfoMapper;
import com.lsstop.service.PageInfoService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 页面信息服务实现类
 *
 * @author lishusheng
 * @date 2025/12/24
 */
@Service
public class PageInfoServiceImpl implements PageInfoService {

    @Resource
    private PageInfoMapper pageManagementMapper;

    /**
     * 获取页面列表
     *
     * @return 页面信息列表
     */
    @Override
    public List<PageInfo> listAllPageInfo() {
        return pageManagementMapper.listAllPageInfo();
    }
}
