package com.lsstop.service;

import com.lsstop.domain.vo.TalkInfoVO;
import com.lsstop.domain.vo.TalkVO;

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
    List<TalkVO> listTalk();

    /**
     * 根据id获取说说详情
     *
     * @param id 说说id
     * @return 说说详情
     */
    TalkInfoVO getTalkById(int id);

}
