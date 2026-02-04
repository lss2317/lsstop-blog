package com.lsstop.service.impl;

import com.lsstop.constant.CommonConst;
import com.lsstop.constant.RedisConst;
import com.lsstop.domain.entity.CommentEntity;
import com.lsstop.domain.entity.UserProfileEntity;
import com.lsstop.domain.entity.WebsiteConfigEntity;
import com.lsstop.domain.vo.AddCommentVO;
import com.lsstop.domain.vo.CommentReplyVO;
import com.lsstop.domain.vo.CommentVO;
import com.lsstop.enums.CommentTypeEnum;
import com.lsstop.enums.IllegalPolicyEnum;
import com.lsstop.mapper.AuthMapper;
import com.lsstop.mapper.CommentMapper;
import com.lsstop.service.CommentService;
import com.lsstop.service.WebsiteConfigService;
import com.lsstop.utils.RedisUtils;
import com.lsstop.utils.SensitiveWordUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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
    private AuthMapper authMapper;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private WebsiteConfigService websiteConfigService;

    /**
     * 新增评论
     *
     * @param comment 评论实体
     * @return 新增的评论信息（含用户资料）
     */
    @Override
    public AddCommentVO insertComment(CommentEntity comment) {
        // 获取敏感词处理策略
        WebsiteConfigEntity config = websiteConfigService.getWebsiteConfig();
        IllegalPolicyEnum policy = IllegalPolicyEnum.of(config.getCommentIllegalPolicy());

        // 敏感词处理
        SensitiveWordUtils.Result result = SensitiveWordUtils.process(comment.getContent(), policy);
        comment.setContent(result.content());

        // 转审核策略且命中敏感词，设置待审核状态
        if (result.hasSensitive() && IllegalPolicyEnum.REVIEW == policy) {
            comment.setReview(CommonConst.REVIEW_PENDING);
        }

        comment.setCreateTime(LocalDateTime.now());

        commentMapper.insertComment(comment);

        // 更新Redis计数
        updateRedisCount(comment);

        // 查询用户资料并组装返回数据
        UserProfileEntity userProfile = authMapper.selectProfileById(comment.getUserId());
        AddCommentVO vo = comment.asViewObject(AddCommentVO.class);
        if (userProfile != null) {
            vo.setAvatar(userProfile.getAvatar());
            vo.setNickname(userProfile.getNickname());
        }
        return vo;
    }

    /**
     * 更新Redis计数
     *
     * @param comment 评论实体
     */
    private void updateRedisCount(CommentEntity comment) {
        if (comment.getParentId() != null) {
            // 回复评论：更新父评论的回复数
            redisUtils.increment(RedisConst.COMMENT_REPLY_COUNT + comment.getParentId());
        }
        // 说说类型的评论（顶级+回复）都需要更新说说评论数
        if (CommentTypeEnum.TALK.getType().equals(comment.getTargetType())) {
            redisUtils.increment(RedisConst.TALK_COMMENT_COUNT + comment.getTargetId());
        }
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
                .toList();
        
        // 批量从 Redis 获取顶级评论回复数和点赞数
        List<String> parentReplyKeys = parentIds.stream()
                .map(id -> RedisConst.COMMENT_REPLY_COUNT + id)
                .collect(Collectors.toList());
        List<String> parentLikeKeys = parentIds.stream()
                .map(id -> RedisConst.COMMENT_LIKE_COUNT + id)
                .collect(Collectors.toList());
        List<Integer> parentReplyCounts = redisUtils.mGet(parentReplyKeys, Integer.class);
        List<Integer> parentLikeCounts = redisUtils.mGet(parentLikeKeys, Integer.class);
        
        // 设置顶级评论回复数和点赞数
        for (int i = 0; i < parentComments.size(); i++) {
            Integer replyCount = parentReplyCounts.get(i);
            Integer likeCount = parentLikeCounts.get(i);
            parentComments.get(i).setReplyCount(replyCount == null ? 0 : replyCount);
            parentComments.get(i).setLikeCount(likeCount == null ? 0 : likeCount);
        }
        
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

    /**
     * 获取子评论列表（分页）
     *
     * @param parentId 父评论id
     * @param current  当前页码
     * @param size     每页数量
     * @param sortType 排序方式：hot=最热, new=最新
     * @return 子评论列表
     */
    @Override
    public List<CommentReplyVO> getReplyList(Integer parentId, Integer current, Integer size, String sortType) {
        int offset = (current - 1) * size;
        return commentMapper.selectReplyList(parentId, sortType, offset, size);
    }
}
