package com.lsstop.service.impl;

import com.lsstop.constant.CommentConst;
import com.lsstop.constant.CommonConst;
import com.lsstop.constant.RedisConst;
import com.lsstop.domain.entity.CommentEntity;
import com.lsstop.domain.entity.UserProfileEntity;
import com.lsstop.domain.entity.WebsiteConfigEntity;
import com.lsstop.domain.vo.AddCommentVO;
import com.lsstop.domain.vo.CommentReplyVO;
import com.lsstop.domain.vo.CommentVO;
import com.lsstop.domain.vo.UserRecentCommentVO;
import com.lsstop.enums.CommentTypeEnum;
import com.lsstop.enums.IllegalPolicyEnum;
import com.lsstop.enums.StatusEnum;
import com.lsstop.exception.BusinessException;
import com.lsstop.mapper.AuthMapper;
import com.lsstop.mapper.CommentMapper;
import com.lsstop.service.CommentService;
import com.lsstop.service.EmailService;
import com.lsstop.service.WebsiteConfigService;
import com.lsstop.utils.RedisUtils;
import com.lsstop.utils.SensitiveWordUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 评论服务实现类
 *
 * @author lishusheng
 * @date 2026/01/28
 */
@Slf4j
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

    @Resource
    private EmailService emailService;

    /**
     * 新增评论
     *
     * @param comment 评论实体
     * @return 新增的评论信息（含用户资料）
     */
    @Override
    public AddCommentVO insertComment(CommentEntity comment) {
        // 获取网站配置
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
        // 待审核评论：清除待审核数缓存
        if (CommonConst.REVIEW_PENDING.equals(comment.getReview())) {
            redisUtils.delete(RedisConst.PENDING_REVIEW_COMMENT_COUNT);
        }

        // 查询用户资料并组装返回数据
        UserProfileEntity userProfile = authMapper.selectProfileById(comment.getUserId());
        AddCommentVO vo = comment.asViewObject(AddCommentVO.class);
        if (userProfile != null) {
            vo.setAvatar(userProfile.getAvatar());
            vo.setNickname(userProfile.getNickname());
        }

        // 发送邮件通知（不需要审核的评论才发送）
        if (CommonConst.REVIEW_NORMAL.equals(comment.getReview()) && CommonConst.ENABLED.equals(config.getEnableCommentEmailNotice())) {
            try {
                emailService.sendCommentNotice(comment, userProfile);
            } catch (Exception e) {
                // 邮件通知失败不能影响评论主流程
                log.warn("评论邮件通知发送失败: {}", e.getMessage());
            }
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
        // 更新今日评论计数
        String todayKey = RedisConst.TODAY_COMMENT_COUNT + LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
        Long val = redisUtils.increment(todayKey);
        if (val != null && val == 1L) {
            redisUtils.expire(todayKey, RedisConst.EXPIRE_ONE_DAY * 2);
        }
        // 清除评论总数与评论来源分布缓存
        redisUtils.delete(RedisConst.TOTAL_COMMENT_COUNT);
        redisUtils.delete(RedisConst.DASHBOARD_COMMENT_SOURCE);
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
        List<CommentReplyVO> replyList = commentMapper.selectReplyList(parentId, sortType, offset, size);
        if (replyList.isEmpty()) {
            return replyList;
        }

        // 批量从 Redis 获取子评论点赞数
        List<String> likeKeys = replyList.stream()
                .map(reply -> RedisConst.COMMENT_LIKE_COUNT + reply.getId())
                .collect(Collectors.toList());
        List<Integer> likeCounts = redisUtils.mGet(likeKeys, Integer.class);

        // 设置子评论点赞数
        for (int i = 0; i < replyList.size(); i++) {
            Integer likeCount = likeCounts.get(i);
            replyList.get(i).setLikeCount(likeCount == null ? 0 : likeCount);
        }

        return replyList;
    }

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @param userId    当前用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Integer commentId, String userId) {
        // 查询评论是否存在
        CommentEntity comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), CommentConst.COMMENT_NOT_FOUND);
        }

        // 校验权限：只有评论作者才能删除
        if (!userId.equals(comment.getUserId())) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), CommentConst.NO_PERMISSION_DELETE_COMMENT);
        }

        long deletedAt = System.currentTimeMillis();

        // 删除评论
        commentMapper.deleteById(commentId, deletedAt);

        // 如果是顶级评论，级联删除所有子评论
        if (comment.getParentId() == null) {
            int deletedChildCount = commentMapper.deleteByParentId(commentId, deletedAt);
            // 删除该评论的回复数缓存
            redisUtils.delete(RedisConst.COMMENT_REPLY_COUNT + commentId);
            // 如果是说说类型，更新说说评论数（顾级评论 + 子评论）
            if (CommentTypeEnum.TALK.getType().equals(comment.getTargetType())) {
                redisUtils.decrement(RedisConst.TALK_COMMENT_COUNT + comment.getTargetId(), 1 + deletedChildCount);
            }
        } else {
            // 子评论：更新父评论的回复数
            redisUtils.decrement(RedisConst.COMMENT_REPLY_COUNT + comment.getParentId());
            // 如果是说说类型，更新说说评论数
            if (CommentTypeEnum.TALK.getType().equals(comment.getTargetType())) {
                redisUtils.decrement(RedisConst.TALK_COMMENT_COUNT + comment.getTargetId());
            }
        }

        // 删除评论点赞数缓存
        redisUtils.delete(RedisConst.COMMENT_LIKE_COUNT + commentId);
        // 清除评论总数与评论来源分布缓存
        redisUtils.delete(RedisConst.TOTAL_COMMENT_COUNT);
        redisUtils.delete(RedisConst.DASHBOARD_COMMENT_SOURCE);
        // 如果删除的是待审核评论，清除待审核数缓存
        if (CommonConst.REVIEW_PENDING.equals(comment.getReview())) {
            redisUtils.delete(RedisConst.PENDING_REVIEW_COMMENT_COUNT);
        }
    }

    /**
     * 获取用户最近评论列表
     *
     * @param userId 用户ID
     * @param limit  限制数量
     * @return 用户最近评论列表
     */
    @Override
    public List<UserRecentCommentVO> getRecentComments(String userId, Integer limit) {
        return commentMapper.selectRecentCommentsByUserId(userId, limit);
    }
}
