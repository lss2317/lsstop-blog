package com.lsstop.mapper;

import com.lsstop.domain.vo.ArticleArchiveVO;

import java.util.List;

/**
 * 文章数据访问层
 *
 * @author lishusheng
 * @date 2026/01/18
 */
public interface ArticleMapper {

    /**
     * 获取文章归档列表
     *
     * @return 文章归档列表
     */
    List<ArticleArchiveVO> getArchiveList();

}
