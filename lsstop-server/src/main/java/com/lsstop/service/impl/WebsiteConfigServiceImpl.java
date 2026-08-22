package com.lsstop.service.impl;

import com.lsstop.constant.RedisConst;
import com.lsstop.constant.WebsiteConfigConst;
import com.lsstop.domain.dto.UpdateWebsiteConfigDTO;
import com.lsstop.domain.entity.WebsiteConfigEntity;
import com.lsstop.domain.vo.RoleVO;
import com.lsstop.domain.vo.VisitStatsVO;
import com.lsstop.enums.StatusEnum;
import com.lsstop.exception.BusinessException;
import com.lsstop.mapper.RoleMapper;
import com.lsstop.mapper.UniqueViewMapper;
import com.lsstop.mapper.WebsiteConfigMapper;
import com.lsstop.service.WebsiteConfigService;
import com.lsstop.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.lsstop.utils.StringUtils.isValidUrl;
import static com.lsstop.utils.StringUtils.isValidWebSocketUrl;

/**
 * 网站配置服务实现类
 *
 * @author lishusheng
 * @date 2025/12/25
 */
@Service
public class WebsiteConfigServiceImpl implements WebsiteConfigService {

    @Resource
    private WebsiteConfigMapper websiteConfigMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private UniqueViewMapper uniqueViewMapper;

    @Resource
    private RedisUtils redisUtils;

    /**
     * 获取网站配置信息，优先从Redis获取，没有则查DB并缓存
     *
     * @return 网站配置实体对象
     */
    @Override
    public WebsiteConfigEntity getWebsiteConfig() {
        // 优先从Redis获取
        WebsiteConfigEntity config = redisUtils.get(RedisConst.WEBSITE_CONFIG, WebsiteConfigEntity.class);
        // 新增配置字段后，旧缓存可能缺少默认角色；此时回源数据库并覆盖旧缓存
        if (config != null && config.getRegisterDefaultRoleId() != null) {
            return config;
        }
        // Redis中没有，查询DB
        config = websiteConfigMapper.getWebsiteConfig();
        if (config != null) {
            // 缓存到1天
            redisUtils.set(RedisConst.WEBSITE_CONFIG, config, RedisConst.EXPIRE_ONE_DAY);
        }
        return config;
    }

    /**
     * 更新网站配置并清除缓存
     *
     * @param dto 网站配置更新参数
     */
    @Override
    public void updateWebsiteConfig(UpdateWebsiteConfigDTO dto) {
        WebsiteConfigEntity existingConfig = websiteConfigMapper.getWebsiteConfig();
        if (existingConfig == null || !Objects.equals(existingConfig.getId(), dto.getId())) {
            throw new BusinessException(StatusEnum.NOT_FOUND, WebsiteConfigConst.WEBSITE_CONFIG_NOT_FOUND);
        }

        // 用户默认角色必须存在且处于启用状态，避免新用户注册后无法获得有效权限
        RoleVO registerDefaultRole = roleMapper.selectRoleById(dto.getRegisterDefaultRoleId());
        if (registerDefaultRole == null || !Integer.valueOf(1).equals(registerDefaultRole.getIsEnabled())) {
            throw new BusinessException(StatusEnum.PARAM_ERROR, WebsiteConfigConst.REGISTER_DEFAULT_ROLE_INVALID);
        }

        // QQ链接为可选项，有值时必须是有效的HTTP或HTTPS地址
        if (StringUtils.isNotBlank(dto.getQqUrl()) && !isValidUrl(dto.getQqUrl())) {
            throw new BusinessException(StatusEnum.PARAM_ERROR, WebsiteConfigConst.QQ_URL_INVALID);
        }

        // GitHub链接为可选项，有值时必须是有效的HTTP或HTTPS地址
        if (StringUtils.isNotBlank(dto.getGithubUrl()) && !isValidUrl(dto.getGithubUrl())) {
            throw new BusinessException(StatusEnum.PARAM_ERROR, WebsiteConfigConst.GITHUB_URL_INVALID);
        }

        // Gitee链接为可选项，有值时必须是有效的HTTP或HTTPS地址
        if (StringUtils.isNotBlank(dto.getGiteeUrl()) && !isValidUrl(dto.getGiteeUrl())) {
            throw new BusinessException(StatusEnum.PARAM_ERROR, WebsiteConfigConst.GITEE_URL_INVALID);
        }

        // WebSocket基础地址为可选项，有值时校验协议、主机和端口格式
        if (StringUtils.isNotBlank(dto.getWebsocketUrl()) && !isValidWebSocketUrl(dto.getWebsocketUrl())) {
            throw new BusinessException(StatusEnum.PARAM_ERROR, WebsiteConfigConst.WEBSOCKET_URL_INVALID
            );
        }

        // 博主头像不是必填项，未传或仅包含空白字符时保留原配置
        String siteAvatar = StringUtils.trimToNull(dto.getSiteAvatar());
        dto.setSiteAvatar(siteAvatar == null ? existingConfig.getSiteAvatar() : siteAvatar);

        // 展示用短文本去除首尾空白，可选地址统一将空字符串转换为null
        dto.setSiteName(StringUtils.trim(dto.getSiteName()));
        dto.setSiteAuthor(StringUtils.trim(dto.getSiteAuthor()));
        dto.setSiteIntro(StringUtils.trim(dto.getSiteIntro()));
        dto.setDefaultUserAvatar(StringUtils.trim(dto.getDefaultUserAvatar()));
        dto.setQqUrl(StringUtils.trimToNull(dto.getQqUrl()));
        dto.setGithubUrl(StringUtils.trimToNull(dto.getGithubUrl()));
        dto.setGiteeUrl(StringUtils.trimToNull(dto.getGiteeUrl()));
        dto.setWebsocketUrl(StringUtils.trimToNull(dto.getWebsocketUrl()));

        websiteConfigMapper.updateWebsiteConfig(dto);
        redisUtils.delete(RedisConst.WEBSITE_CONFIG);
    }

    /**
     * 上报访问并获取访问统计
     * 同一IP每3小时计数一次
     *
     * @param ipAddress 访客IP地址
     * @return 访问统计信息
     */
    @Override
    public VisitStatsVO reportVisit(String ipAddress) {
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String uvKey = RedisConst.UNIQUE_VISITOR + ipAddress;

        // 检查该IP是否3小时内已访问，未访问则增加访问量
        Boolean isNew = redisUtils.setIfAbsent(uvKey, 1, 3 * RedisConst.EXPIRE_ONE_HOUR, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(isNew)) {
            // 增加今日访问量（expire每次调用可确保TTL不丢失）
            String todayKey = RedisConst.TODAY_VIEW_COUNT + today;
            redisUtils.increment(todayKey);
            redisUtils.expire(todayKey, RedisConst.EXPIRE_ONE_DAY + RedisConst.EXPIRE_TWO_HOURS);
        }

        // 记录今日独立访客（每个IP每天只计一次）
        String uvSetKey = RedisConst.TODAY_UV_SET + today;
        Long uvAdded = redisUtils.sAdd(uvSetKey, ipAddress);
        if (uvAdded != null && uvAdded > 0) {
            // 新IP加入时刷新过期时间，确保TTL不丢失
            redisUtils.expire(uvSetKey, RedisConst.EXPIRE_ONE_DAY + RedisConst.EXPIRE_TWO_HOURS);
        } else {
            // 防御性检查：如果key存在但无TTL（极端情况），补设过期时间
            Long ttl = redisUtils.getExpire(uvSetKey);
            if (ttl != null && ttl == -1) {
                redisUtils.expire(uvSetKey, RedisConst.EXPIRE_ONE_DAY + RedisConst.EXPIRE_TWO_HOURS);
            }
        }

        // 获取历史总访问量，优先从Redis取
        Integer historyCount = redisUtils.get(RedisConst.HISTORY_VIEW_COUNT, Integer.class);
        if (historyCount == null) {
            historyCount = uniqueViewMapper.getTotalViewsCount();
            if (historyCount == null) {
                historyCount = 0;
            }
            // 缓存历史总量，设置2天过期兜底（定时任务每天会主动刷新）
            redisUtils.set(RedisConst.HISTORY_VIEW_COUNT, historyCount, 2 * RedisConst.EXPIRE_ONE_DAY);
        }

        // 补充昨日未同步的访问量（解决凌晨00:00~01:00数据窗口问题）
        String yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE);
        Integer yesterdayCount = redisUtils.get(RedisConst.TODAY_VIEW_COUNT + yesterday, Integer.class);
        if (yesterdayCount != null) {
            historyCount = historyCount + yesterdayCount;
        }

        // 获取今日访问量
        Integer todayCount = redisUtils.get(RedisConst.TODAY_VIEW_COUNT + today, Integer.class);
        if (todayCount == null) {
            todayCount = 0;
        }

        // 获取今日独立访客数
        Long todayUvCount = redisUtils.sSize(RedisConst.TODAY_UV_SET + today);
        Integer todayUv = (todayUvCount != null) ? todayUvCount.intValue() : 0;

        return VisitStatsVO.builder()
                .viewsCount(historyCount + todayCount)
                .todayUniqueVisitorCount(todayUv)
                .build();
    }

}
