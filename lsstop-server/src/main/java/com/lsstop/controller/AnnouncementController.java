package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.common.Result;
import com.lsstop.domain.vo.AnnouncementVO;
import com.lsstop.service.AnnouncementService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 公告控制层
 *
 * @author lishusheng
 * @date 2026/02/20
 */
@RestController
public class AnnouncementController {

    @Resource
    private AnnouncementService announcementService;

    /**
     * 获取公告列表
     *
     * @return 公告列表
     */
    @GetMapping("/front/announcement/listAnnouncement")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<List<AnnouncementVO>> listAnnouncement() {
        return Result.success(announcementService.listAnnouncement());
    }

}
