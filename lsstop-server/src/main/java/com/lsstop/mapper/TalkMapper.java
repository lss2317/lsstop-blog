package com.lsstop.mapper;

import com.lsstop.domain.vo.TalkVO;
import org.apache.ibatis.annotations.Param;

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
     * 根据ID查询说说内容
     *
     * @param id 说说ID
     * @return 说说内容
     */
    String selectContentById(@Param("id") Integer id);

}
