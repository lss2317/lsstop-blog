package com.lsstop.service.impl;

import com.lsstop.domain.entity.PageManagement;
import com.lsstop.mapper.PageManagementMapper;
import com.lsstop.service.PageManagementService;
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
public class PageManagementImpl implements PageManagementService {

    @Resource
    private PageManagementMapper pageManagementMapper;

    /**
     * 获取页面列表
     *
     * @return 页面信息列表
     */
    @Override
    public List<PageManagement> listAllPageManagement() {
        return pageManagementMapper.listAllPageManagement();
    }
}
