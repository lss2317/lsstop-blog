package com.lsstop.mapper;

import com.lsstop.domain.vo.LikeCountVO;
import com.lsstop.domain.entity.LikeRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 点赞数据访问层
 *
 * @author lishusheng
 * @date 2026/01/03
 */
@Mapper
public interface LikeMapper {

    /**
     * 查询所有有效的点赞记录
     *
     * @return 点赞记录列表
     */
    List<LikeRecordEntity> listValidLikes();

    /**
     * 按类型统计各目标的点赞数
     *
     * @param type 点赞类型
     * @return 点赞统计列表
     */
    List<LikeCountVO> countLikesByType(@Param("type") Integer type);

    /**
     * 批量插入或更新点赞记录
     *
     * @param records 点赞记录列表
     */
    void batchInsertOrUpdate(@Param("records") List<LikeRecordEntity> records);

    /**
     * 统计用户获赞数量（评论被点赞数）
     *
     * @param userId 用户ID
     * @return 获赞数量
     */
    Integer countLikesByUserId(@Param("userId") String userId);

}
