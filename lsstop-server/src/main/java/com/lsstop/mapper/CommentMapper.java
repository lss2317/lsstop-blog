package com.lsstop.mapper;

import com.lsstop.domain.entity.CommentEntity;
import com.lsstop.domain.vo.CommentCountVO;
import com.lsstop.domain.vo.CommentReplyVO;
import com.lsstop.domain.vo.CommentVO;
import com.lsstop.domain.vo.UserRecentCommentVO;
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

    /**
     * 查询顶级评论列表（分页）
     *
     * @param typeId   目标id
     * @param type     目标类型
     * @param offset   偏移量
     * @param size     每页数量
     * @param sortType 排序方式：hot=最热, new=最新
     * @return 顶级评论列表
     */
    List<CommentVO> selectParentComments(@Param("typeId") Integer typeId,
                                         @Param("type") Integer type,
                                         @Param("offset") Integer offset,
                                         @Param("size") Integer size,
                                         @Param("sortType") String sortType);

    /**
     * 统计评论总数
     *
     * @param typeId 目标id
     * @param type   目标类型
     * @return 评论总数
     */
    Integer countComments(@Param("typeId") Integer typeId,
                          @Param("type") Integer type);

    /**
     * 统计每条评论的回复数
     *
     * @return 评论回复数列表
     */
    List<CommentCountVO> countRepliesByParent();

    /**
     * 查询单个父评论的子评论列表（分页）
     *
     * @param parentId 父评论id
     * @param sortType 排序方式：hot=最热, new=最新
     * @param offset   偏移量
     * @param size     每页数量
     * @return 子评论列表
     */
    List<CommentReplyVO> selectReplyList(@Param("parentId") Integer parentId,
                                         @Param("sortType") String sortType,
                                         @Param("offset") Integer offset,
                                         @Param("size") Integer size);

    /**
     * 根据ID查询评论
     *
     * @param id 评论ID
     * @return 评论实体
     */
    CommentEntity selectById(@Param("id") Integer id);

    /**
     * 按审核状态统计父评论下未删除的子评论数。
     *
     * @param parentId 父评论ID
     * @param review   审核状态
     * @return 子评论数量
     */
    int countChildrenByReview(@Param("parentId") Integer parentId, @Param("review") Integer review);

    /**
     * 软删除评论
     *
     * @param id        评论ID
     * @param deletedAt 删除时间戳
     */
    void deleteById(@Param("id") Integer id, @Param("deletedAt") Long deletedAt);

    /**
     * 软删除某个父评论下的所有子评论
     *
     * @param parentId  父评论ID
     * @param deletedAt 删除时间戳
     * @return 删除的子评论数量
     */
    int deleteByParentId(@Param("parentId") Integer parentId, @Param("deletedAt") Long deletedAt);

    /**
     * 统计用户的评论数量
     *
     * @param userId 用户ID
     * @return 评论数量
     */
    Integer countByUserId(@Param("userId") String userId);

    /**
     * 查询用户最近评论列表
     *
     * @param userId 用户ID
     * @param limit  限制数量
     * @return 用户最近评论列表
     */
    List<UserRecentCommentVO> selectRecentCommentsByUserId(@Param("userId") String userId,
                                                           @Param("limit") Integer limit);
}
