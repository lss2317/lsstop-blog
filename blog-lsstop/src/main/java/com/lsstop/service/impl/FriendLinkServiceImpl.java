package com.lsstop.service.impl;

import com.lsstop.domain.entity.FriendLink;
import com.lsstop.mapper.FriendLinkMapper;
import com.lsstop.service.FriendLinkService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 友链服务实现类
 *
 * @author lishusheng
 * @date 2025/12/27
 */
@Service
public class FriendLinkServiceImpl implements FriendLinkService {

    @Resource
    private FriendLinkMapper friendLinkMapper;

    /**
     * 获取友链列表
     *
     * @return 友链列表
     */
    @Override
    public List<FriendLink> getFriendLinkList() {
        return friendLinkMapper.getFriendLinkList();
    }
}
