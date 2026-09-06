package com.lsstop.service.impl;

import com.lsstop.constant.AnnouncementConst;
import com.lsstop.constant.RedisConst;
import com.lsstop.domain.dto.AddAnnouncementDTO;
import com.lsstop.domain.dto.UpdateAnnouncementDTO;
import com.lsstop.domain.entity.AnnouncementEntity;
import com.lsstop.domain.vo.AnnouncementAdminVO;
import com.lsstop.domain.vo.AnnouncementVO;
import com.lsstop.enums.StatusEnum;
import com.lsstop.exception.BusinessException;
import com.lsstop.mapper.AnnouncementMapper;
import com.lsstop.service.AnnouncementService;
import com.lsstop.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    @Resource
    private RedisUtils redisUtils;

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
    @Override
    public List<AnnouncementAdminVO> listAdminAnnouncements(Integer current, Integer size, String keyword,
                                                             Integer type, Integer isEnabled) {
        int offset = (current - 1) * size;
        return announcementMapper.selectAdminAnnouncementList(offset, size, keyword, type, isEnabled);
    }

    /**
     * 统计后台公告总数
     *
     * @param keyword   公告标题关键词
     * @param type      展示位置
     * @param isEnabled 是否启用
     * @return 公告总数
     */
    @Override
    public Integer countAdminAnnouncementTotal(String keyword, Integer type, Integer isEnabled) {
        return announcementMapper.countAdminAnnouncementTotal(keyword, type, isEnabled);
    }

    /**
     * 新增公告
     *
     * @param dto 新增公告参数
     */
    @Override
    public void addAnnouncement(AddAnnouncementDTO dto) {
        validateEffectiveTime(dto.getStartTime(), dto.getEndTime());
        announcementMapper.insertAnnouncement(dto);
        clearAnnouncementCache();
    }

    /**
     * 编辑公告
     *
     * @param dto 编辑公告参数
     */
    @Override
    public void updateAnnouncement(UpdateAnnouncementDTO dto) {
        validateAnnouncementExists(dto.getId());
        validateEffectiveTime(dto.getStartTime(), dto.getEndTime());
        announcementMapper.updateAnnouncement(dto);
        clearAnnouncementCache();
    }

    /**
     * 删除公告
     *
     * @param id 公告ID
     */
    @Override
    public void deleteAnnouncement(Integer id) {
        validateAnnouncementExists(id);
        announcementMapper.deleteById(id, System.currentTimeMillis());
        clearAnnouncementCache();
    }

    /**
     * 获取有效的公告列表
     *
     * @return 公告列表
     */
    @Override
    public List<AnnouncementVO> listAnnouncement() {
        List<AnnouncementEntity> announcementList = redisUtils.getList(RedisConst.ANNOUNCEMENT_LIST, AnnouncementEntity.class);
        if (announcementList == null) {
            announcementList = announcementMapper.listEnabledAnnouncements();
            redisUtils.set(RedisConst.ANNOUNCEMENT_LIST, announcementList, RedisConst.EXPIRE_ONE_HOUR);
        }

        LocalDateTime now = LocalDateTime.now();
        return announcementList.stream()
                .filter(item -> item.getStartTime() == null || !item.getStartTime().isAfter(now))
                .filter(item -> item.getEndTime() == null || !item.getEndTime().isBefore(now))
                .map(item -> item.asViewObject(AnnouncementVO.class))
                .toList();
    }

    /**
     * 校验公告是否存在
     *
     * @param id 公告ID
     */
    private void validateAnnouncementExists(Integer id) {
        if (announcementMapper.selectAdminAnnouncementById(id) == null) {
            throw new BusinessException(StatusEnum.NOT_FOUND, AnnouncementConst.ANNOUNCEMENT_NOT_FOUND);
        }
    }

    /**
     * 校验生效时间范围
     *
     * @param startTime 生效开始时间
     * @param endTime   生效结束时间
     */
    private void validateEffectiveTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime != null && endTime != null && endTime.isBefore(startTime)) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), AnnouncementConst.INVALID_EFFECTIVE_TIME);
        }
    }

    /**
     * 清理前台公告列表缓存
     */
    private void clearAnnouncementCache() {
        redisUtils.delete(RedisConst.ANNOUNCEMENT_LIST);
    }

}
