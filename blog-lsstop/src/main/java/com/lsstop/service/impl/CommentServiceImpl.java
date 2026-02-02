package com.lsstop.service.impl;

import com.lsstop.constant.RedisConst;
import com.lsstop.domain.entity.CommentEntity;
import com.lsstop.domain.vo.CommentReplyVO;
import com.lsstop.domain.vo.CommentVO;
import com.lsstop.mapper.CommentMapper;
import com.lsstop.service.CommentService;
import com.lsstop.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Resource
    private RedisUtils redisUtils;

    /**
     * 新增评论
     *
     * @param comment 评论实体
     */
    @Override
    public void insertComment(CommentEntity comment) {
        commentMapper.insertComment(comment);
    }

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
    @Override
    public List<CommentVO> getCommentList(Integer typeId, Integer type, Integer current, Integer size, String sortType) {
        int offset = (current - 1) * size;
        // 查询顶级评论
        List<CommentVO> parentComments = commentMapper.selectParentComments(typeId, type, offset, size, sortType);
        if (parentComments.isEmpty()) {
            return parentComments;
        }
        // 获取顶级评论ID列表
        List<Integer> parentIds = parentComments.stream()
                .map(CommentVO::getId)
                .collect(Collectors.toList());
        // 查询子评论
        List<CommentReplyVO> childComments = commentMapper.selectChildComments(parentIds);
        
        // 批量从Redis获取顶级评论回复数
        List<String> parentReplyKeys = parentIds.stream()
                .map(id -> RedisConst.COMMENT_REPLY_COUNT + id)
                .collect(Collectors.toList());
        List<Integer> parentReplyCounts = redisUtils.mGet(parentReplyKeys, Integer.class);
        
        // 设置顶级评论回复数
        for (int i = 0; i < parentComments.size(); i++) {
            Integer replyCount = parentReplyCounts.get(i);
            parentComments.get(i).setReplyCount(replyCount == null ? 0 : replyCount);
        }
        
        // 批量从Redis获取子评论点赞数
        List<String> childLikeKeys = childComments.stream()
                .map(reply -> RedisConst.COMMENT_LIKE_COUNT + reply.getId())
                .collect(Collectors.toList());
        List<Integer> childLikeCounts = childLikeKeys.isEmpty() ? 
                new ArrayList<>() : redisUtils.mGet(childLikeKeys, Integer.class);
        
        // 设置子评论点赞数
        for (int i = 0; i < childComments.size(); i++) {
            Integer likeCount = childLikeCounts.get(i);
            childComments.get(i).setLikeCount(likeCount == null ? 0 : likeCount);
        }
        
        // 组装子评论到父评论
        Map<Integer, List<CommentReplyVO>> childMap = childComments.stream()
                .collect(Collectors.groupingBy(CommentReplyVO::getParentId));
        parentComments.forEach(parent -> {
            List<CommentReplyVO> replyList = childMap.getOrDefault(parent.getId(), new ArrayList<>());
            parent.setReplyList(replyList);
        });
        return parentComments;
    }

    /**
     * 获取评论总数
     *
     * @param typeId 目标id
     * @param type   目标类型
     * @return 评论总数
     */
    @Override
    public Integer getCommentCount(Integer typeId, Integer type) {
        return commentMapper.countComments(typeId, type);
    }
}
