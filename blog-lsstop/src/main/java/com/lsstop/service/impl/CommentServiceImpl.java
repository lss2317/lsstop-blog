package com.lsstop.service.impl;

import com.lsstop.domain.entity.CommentEntity;
import com.lsstop.mapper.CommentMapper;
import com.lsstop.service.CommentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 评论服务实现类
 *
 * @author lishusheng
 * @date 2026/01/28
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Resource
    private CommentMapper commentMapper;

    /**
     * 新增评论
     *
     * @param comment 评论实体
     */
    @Override
    public void insertComment(CommentEntity comment) {
        commentMapper.insertComment(comment);
    }
}
