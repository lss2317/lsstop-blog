package com.lsstop.service;

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
     * 获取有效的公告列表
     *
     * @return 公告列表
     */
    List<AnnouncementVO> listAnnouncement();

}
