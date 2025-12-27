package com.lsstop.mapper;

import com.lsstop.domain.entity.FriendLink;

import java.util.List;

/**
 * 友链数据访问层
 *
 * @author lishusheng
 * @date 2025/12/27
 */
public interface FriendLinkMapper {

    /**
     * 获取友链列表
     *
     * @return 友链列表
     */
    List<FriendLink> getFriendLinkList();

}
