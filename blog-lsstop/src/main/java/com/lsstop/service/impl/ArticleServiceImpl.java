package com.lsstop.service.impl;

import com.lsstop.domain.vo.ArticleArchiveVO;
import com.lsstop.mapper.ArticleMapper;
import com.lsstop.service.ArticleService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 文章服务实现类
 *
 * @author lishusheng
 * @date 2026/01/18
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    @Resource
    private ArticleMapper articleMapper;

    /**
     * 获取文章归档列表
     *
     * @return 文章归档列表
     */
    @Override
    public List<ArticleArchiveVO> getArchiveList() {
        return articleMapper.getArchiveList();
    }

}
