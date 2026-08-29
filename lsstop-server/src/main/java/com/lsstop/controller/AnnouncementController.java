package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.annotation.OperationLog;
import com.lsstop.common.Result;
import com.lsstop.constant.CommentConst;
import com.lsstop.domain.dto.AddAnnouncementDTO;
import com.lsstop.domain.dto.UpdateAnnouncementDTO;
import com.lsstop.domain.vo.AnnouncementAdminPageVO;
import com.lsstop.domain.vo.AnnouncementVO;
import com.lsstop.enums.OperationModuleEnum;
import com.lsstop.enums.OperationTypeEnum;
import com.lsstop.enums.StatusEnum;
import com.lsstop.exception.BusinessException;
import com.lsstop.service.AnnouncementService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
     * 获取后台公告列表（分页）
     *
     * @param current   当前页码
     * @param size      每页条数
     * @param keyword   公告标题关键词
     * @param type      展示位置：1-弹窗公告 2-首页展示 3-两者都有
     * @param isEnabled 是否启用：0-禁用 1-启用
     * @return 公告列表及总数
     */
    @GetMapping("/admin/announcement/list")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<AnnouncementAdminPageVO> listAdminAnnouncement(@RequestParam Integer current,
                                                                  @RequestParam Integer size,
                                                                  @RequestParam(required = false) String keyword,
                                                                  @RequestParam(required = false) Integer type,
                                                                  @RequestParam(required = false) Integer isEnabled) {
        if (current < 1) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), CommentConst.INVALID_PAGE_PARAM);
        }
        if (size < 1) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), CommentConst.INVALID_PAGE_PARAM);
        }
        AnnouncementAdminPageVO pageVO = new AnnouncementAdminPageVO(
                announcementService.listAdminAnnouncements(current, size, keyword, type, isEnabled),
                current, size, announcementService.countAdminAnnouncementTotal(keyword, type, isEnabled)
        );
        return Result.success(pageVO);
    }

    /**
     * 新增公告
     *
     * @param dto 新增公告参数
     * @return 操作结果
     */
    @PostMapping("/admin/announcement/add")
    @AccessLimit(seconds = 60, maxCount = 30)
    @OperationLog(module = OperationModuleEnum.ANNOUNCEMENT, type = OperationTypeEnum.ADD, description = "新增公告")
    public Result<Void> addAnnouncement(@RequestBody @Validated AddAnnouncementDTO dto) {
        announcementService.addAnnouncement(dto);
        return Result.success();
    }

    /**
     * 编辑公告
     *
     * @param dto 编辑公告参数
     * @return 操作结果
     */
    @PutMapping("/admin/announcement/update")
    @AccessLimit(seconds = 60, maxCount = 30)
    @OperationLog(module = OperationModuleEnum.ANNOUNCEMENT, type = OperationTypeEnum.UPDATE, description = "编辑公告")
    public Result<Void> updateAnnouncement(@RequestBody @Validated UpdateAnnouncementDTO dto) {
        announcementService.updateAnnouncement(dto);
        return Result.success();
    }

    /**
     * 删除公告
     *
     * @param id 公告ID
     * @return 操作结果
     */
    @DeleteMapping("/admin/announcement/delete/{id}")
    @AccessLimit(seconds = 60, maxCount = 30)
    @OperationLog(module = OperationModuleEnum.ANNOUNCEMENT, type = OperationTypeEnum.DELETE, description = "删除公告")
    public Result<Void> deleteAnnouncement(@PathVariable Integer id) {
        announcementService.deleteAnnouncement(id);
        return Result.success();
    }

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
