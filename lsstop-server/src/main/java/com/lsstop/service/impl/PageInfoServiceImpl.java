package com.lsstop.service.impl;

import com.lsstop.constant.RedisConst;
import com.lsstop.domain.entity.PageInfoEntity;
import com.lsstop.mapper.PageInfoMapper;
import com.lsstop.service.PageInfoService;
import com.lsstop.utils.RedisUtils;
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

    @Resource
    private RedisUtils redisUtils;

    /**
     * 获取页面列表
     *
     * @return 页面信息列表
     */
    @Override
    public List<PageInfoEntity> getPageInfoList() {
        // 先从缓存获取
        List<PageInfoEntity> pageInfoList = redisUtils.getList(RedisConst.PAGE_INFO_LIST, PageInfoEntity.class);
        if (pageInfoList != null) {
            return pageInfoList;
        }
        // 缓存不存在，查询数据库
        pageInfoList = pageManagementMapper.getPageInfoList();
        // 写入缓存，过期时间1天
        redisUtils.set(RedisConst.PAGE_INFO_LIST, pageInfoList, RedisConst.EXPIRE_ONE_DAY);
        return pageInfoList;
    }
}
