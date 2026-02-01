package com.lsstop.service;

import com.lsstop.domain.entity.CommentEntity;
import com.lsstop.domain.vo.CommentVO;

import java.util.List;

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

    /**
     * 获取评论列表（分页）
     *
     * @param typeId   目标id
     * @param type     目标类型
     * @param current  当前页码
     * @param size     每页数量
     * @param sortType 排序方式：hot=最热, new=最新
     * @return 评论列表
     */
    List<CommentVO> getCommentList(Integer typeId, Integer type, Integer current, Integer size, String sortType);

    /**
     * 获取评论总数
     *
     * @param typeId 目标id
     * @param type   目标类型
     * @return 评论总数
     */
    Integer getCommentCount(Integer typeId, Integer type);
}
