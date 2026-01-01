package com.lsstop.service;

import com.lsstop.domain.vo.TalkVo;

import java.util.List;

/**
 * 说说服务
 *
 * @author lishusheng
 * @date 2026/01/01
 */
public interface TalkService {

    /**
     * 获取说说列表
     *
     * @return 说说视图对象列表
     */
    List<TalkVo> listTalk();

    /**
     * 根据id获取说说
     *
     * @param id 说说id
     * @return 说说视图对象
     */
    TalkVo getTalkById(int id);

}
