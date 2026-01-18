package com.lsstop.service;

import com.lsstop.domain.vo.ArticleArchiveVO;

import java.util.List;

/**
 * 文章服务
 *
 * @author lishusheng
 * @date 2026/01/18
 */
public interface ArticleService {

    /**
     * 获取文章归档列表
     *
     * @return 文章归档列表
     */
    List<ArticleArchiveVO> getArchiveList();

}
