package com.lsstop.mapper;

import com.lsstop.domain.entity.CommentEntity;
import com.lsstop.domain.vo.CommentCountVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论数据访问层
 *
 * @author lishusheng
 * @date 2026/01/03
 */
@Mapper
public interface CommentMapper {

    /**
     * 按目标类型统计各目标的评论数
     *
     * @param targetType 目标类型（1文章 2友链 3说说）
     * @return 评论统计列表
     */
    List<CommentCountVO> countCommentsByTargetType(@Param("targetType") Integer targetType);

    /**
     * 新增评论
     *
     * @param comment 评论实体
     */
    void insertComment(CommentEntity comment);
}
