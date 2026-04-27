package com.lsstop.service;

import com.lsstop.domain.vo.TagVO;

import java.util.List;

/**
 * 标签服务
 *
 * @author lishusheng
 * @date 2026/01/11
 */
public interface TagService {

    /**
     * 获取标签列表
     *
     * @return 标签列表
     */
    List<TagVO> getTagList();

}
