package com.lsstop.service;

import com.lsstop.domain.entity.FriendLink;

import java.util.List;

/**
 * 友链服务
 *
 * @author lishusheng
 * @date 2025/12/27
 */
public interface FriendLinkService {

    /**
     * 获取友链列表
     *
     * @return 友链列表
     */
    List<FriendLink> getFriendLinkList();

}
