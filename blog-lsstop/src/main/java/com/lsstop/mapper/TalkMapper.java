package com.lsstop.mapper;

import com.lsstop.domain.dataObject.TalkStatsDO;
import com.lsstop.domain.vo.TalkVO;

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
    List<TalkVO> listTalk();

    /**
     * 根据id查询说说
     *
     * @param id 说说id
     * @return 说说
     */
    TalkVO getTalkById(Integer id);

    /**
     * 统计所有说说的点赞数和评论数
     *
     * @return 说说统计数据列表
     */
    List<TalkStatsDO> countTalkStats();

}
