package com.lsstop.mapper;

import com.lsstop.domain.dto.AddAnnouncementDTO;
import com.lsstop.domain.dto.UpdateAnnouncementDTO;
import com.lsstop.domain.entity.AnnouncementEntity;
import com.lsstop.domain.vo.AnnouncementAdminVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 公告数据访问层
 *
 * @author lishusheng
 * @date 2026/02/20
 */
public interface AnnouncementMapper {

    /**
     * 分页查询后台公告列表
     *
     * @param offset    偏移量
     * @param size      每页条数
     * @param keyword   公告标题关键词
     * @param type      展示位置
     * @param isEnabled 是否启用
     * @return 公告列表
     */
    List<AnnouncementAdminVO> selectAdminAnnouncementList(@Param("offset") Integer offset,
                                                           @Param("size") Integer size,
                                                           @Param("keyword") String keyword,
                                                           @Param("type") Integer type,
                                                           @Param("isEnabled") Integer isEnabled);

    /**
     * 统计后台公告总数
     *
     * @param keyword   公告标题关键词
     * @param type      展示位置
     * @param isEnabled 是否启用
     * @return 公告总数
     */
    Integer countAdminAnnouncementTotal(@Param("keyword") String keyword,
                                         @Param("type") Integer type,
                                         @Param("isEnabled") Integer isEnabled);

    /**
     * 根据ID查询后台公告
     *
     * @param id 公告ID
     * @return 公告信息
     */
    AnnouncementAdminVO selectAdminAnnouncementById(@Param("id") Integer id);

    /**
     * 新增公告
     *
     * @param dto 新增公告参数
     */
    void insertAnnouncement(AddAnnouncementDTO dto);

    /**
     * 编辑公告
     *
     * @param dto 编辑公告参数
     */
    void updateAnnouncement(UpdateAnnouncementDTO dto);

    /**
     * 软删除公告
     *
     * @param id        公告ID
     * @param deletedAt 删除时间戳
     */
    void deleteById(@Param("id") Integer id, @Param("deletedAt") Long deletedAt);

    /**
     * 获取所有启用公告
     *
     * @return 公告列表
     */
    List<AnnouncementEntity> listEnabledAnnouncements();

}
