package com.lsstop.mapper;

import com.lsstop.domain.vo.TalkVo;

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
    List<TalkVo> listTalk();

    /**
     * 根据id查询说说
     *
     * @param id 说说id
     * @return 说说
     */
    TalkVo getTalkById(Integer id);

}
