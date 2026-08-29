package com.lsstop.service.impl;

import com.lsstop.constant.CommonConst;
import com.lsstop.constant.RedisConst;
import com.lsstop.domain.entity.MessageEntity;
import com.lsstop.domain.entity.WebsiteConfigEntity;
import com.lsstop.enums.IllegalPolicyEnum;
import com.lsstop.mapper.MessageMapper;
import com.lsstop.service.MessageService;
import com.lsstop.service.WebsiteConfigService;
import com.lsstop.utils.RedisUtils;
import com.lsstop.utils.SensitiveWordUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 留言服务实现类
 *
 * @author lishusheng
 * @date 2025/12/21
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Resource
    private MessageMapper messageMapper;

    @Resource
    private WebsiteConfigService websiteConfigService;

    @Resource
    private RedisUtils redisUtils;

    /**
     * 前台获取留言数据
     *
     * @return 留言列表
     */
    @Override
    public List<MessageEntity> getMessageList() {
        return messageMapper.getMessageList();
    }

    /**
     * 新增留言
     *
     * @param message 留言实体
     * @return 新增的留言实体（含过滤后内容）
     */
    @Override
    public MessageEntity insertMessage(MessageEntity message) {
        // 获取敏感词处理策略
        WebsiteConfigEntity config = websiteConfigService.getWebsiteConfig();
        IllegalPolicyEnum policy = IllegalPolicyEnum.of(config.getMessageIllegalPolicy());

        // 敏感词处理
        SensitiveWordUtils.Result result = SensitiveWordUtils.process(message.getMessageContent(), policy);
        message.setMessageContent(result.content());

        // 转审核策略且命中敏感词，设置待审核状态
        if (result.hasSensitive() && IllegalPolicyEnum.REVIEW == policy) {
            message.setReview(CommonConst.REVIEW_PENDING);
        }

        messageMapper.insertMessage(message);
        // 只有审核通过的留言才进入公开计数和仪表盘统计
        if (CommonConst.REVIEW_NORMAL.equals(message.getReview())) {
            redisUtils.delete(RedisConst.TOTAL_MESSAGE_COUNT);
            String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
            redisUtils.delete(List.of(
                    RedisConst.DASHBOARD_TODAY_MESSAGE_COUNT + today,
                    RedisConst.DASHBOARD_INTERACTION_TREND + today
            ));
        }
        // 待审核留言：清除待审核数缓存
        if (CommonConst.REVIEW_PENDING.equals(message.getReview())) {
            redisUtils.delete(RedisConst.PENDING_REVIEW_MESSAGE_COUNT);
        }
        return message;
    }
}
