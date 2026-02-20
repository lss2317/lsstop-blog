package com.lsstop.service.impl;

import com.lsstop.domain.vo.AnnouncementVO;
import com.lsstop.mapper.AnnouncementMapper;
import com.lsstop.service.AnnouncementService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 公告服务实现类
 *
 * @author lishusheng
 * @date 2026/02/20
 */
@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Resource
    private AnnouncementMapper announcementMapper;

    /**
     * 获取有效的公告列表
     *
     * @return 公告列表
     */
    @Override
    public List<AnnouncementVO> listAnnouncement() {
        return announcementMapper.listAnnouncement();
    }

}
