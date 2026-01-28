package com.lsstop.service;

import com.lsstop.domain.entity.CommentEntity;

/**
 * 评论服务接口
 *
 * @author lishusheng
 * @date 2026/01/28
 */
public interface CommentService {

    /**
     * 新增评论
     *
     * @param comment 评论实体
     */
    void insertComment(CommentEntity comment);
}
