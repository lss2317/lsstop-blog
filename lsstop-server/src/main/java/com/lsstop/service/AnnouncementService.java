package com.lsstop.service;

import com.lsstop.domain.dto.AddAnnouncementDTO;
import com.lsstop.domain.dto.UpdateAnnouncementDTO;
import com.lsstop.domain.vo.AnnouncementAdminVO;
import com.lsstop.domain.vo.AnnouncementVO;

import java.util.List;

/**
 * 公告服务
 *
 * @author lishusheng
 * @date 2026/02/20
 */
public interface AnnouncementService {

    /**
     * 分页查询后台公告列表
     *
     * @param current   当前页码
     * @param size      每页条数
     * @param keyword   公告标题关键词
     * @param type      展示位置
     * @param isEnabled 是否启用
     * @return 公告列表
     */
    List<AnnouncementAdminVO> listAdminAnnouncements(Integer current, Integer size, String keyword,
                                                       Integer type, Integer isEnabled);

    /**
     * 统计后台公告总数
     *
     * @param keyword   公告标题关键词
     * @param type      展示位置
     * @param isEnabled 是否启用
     * @return 公告总数
     */
    Integer countAdminAnnouncementTotal(String keyword, Integer type, Integer isEnabled);

    /**
     * 新增公告
     *
     * @param dto 新增公告参数
     */
    void addAnnouncement(AddAnnouncementDTO dto);

    /**
     * 编辑公告
     *
     * @param dto 编辑公告参数
     */
    void updateAnnouncement(UpdateAnnouncementDTO dto);

    /**
     * 删除公告
     *
     * @param id 公告ID
     */
    void deleteAnnouncement(Integer id);

    /**
     * 获取有效的公告列表
     *
     * @return 公告列表
     */
    List<AnnouncementVO> listAnnouncement();

}
