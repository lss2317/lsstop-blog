package com.lsstop.mapper;

import com.lsstop.domain.entity.Talk;

import java.util.List;

/**
 * 说说数据访问层
 *
 * @author lishusheng
 * @date 2026/01/01
 */
public interface TalkMapper {

    /**
     * 查询说说列表
     *
     * @return 说说列表
     */
    List<Talk> listTalk();

}
