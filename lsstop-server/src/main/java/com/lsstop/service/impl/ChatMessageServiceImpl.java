package com.lsstop.service.impl;

import com.alibaba.fastjson2.JSON;
import com.lsstop.constant.ChatConst;
import com.lsstop.domain.dto.ChatMessageDTO;
import com.lsstop.domain.entity.ChatMessageEntity;
import com.lsstop.domain.vo.ChatMessageVO;
import com.lsstop.enums.StatusEnum;
import com.lsstop.exception.BusinessException;
import com.lsstop.mapper.ChatMessageMapper;
import com.lsstop.service.ChatMessageService;
import com.lsstop.utils.IpUtils;
import com.lsstop.utils.SensitiveWordUtils;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 聊天消息服务实现类
 *
 * @author lishusheng
 * @date 2026/04/04
 */
@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    @Resource
    private ChatMessageMapper chatMessageMapper;

    /**
     * 获取聊天消息列表（游标分页）
     *
     * @param lastId 上一页最后一条消息的id，首次传null
     * @param size   每页大小
     * @return 聊天消息列表
     */
    @Override
    public List<ChatMessageVO> listMessage(Integer lastId, Integer size) {
        List<ChatMessageVO> list = chatMessageMapper.listMessage(lastId, size);
        list.forEach(vo -> {
            if (vo.getImagesJson() != null) {
                vo.setImages(JSON.parseArray(vo.getImagesJson(), String.class));
            }
        });
        // SQL按id desc查询用于游标分页取最近N条，反转为时间正序返回前端
        Collections.reverse(list);
        return list;
    }

    /**
     * 发送聊天消息（含参数校验、构建实体、持久化）
     *
     * @param dto       聊天消息请求参数
     * @param userId    用户id
     * @param ipAddress 用户IP地址
     * @return 新增的消息实体（含id）
     */
    @Override
    public ChatMessageEntity sendMessage(ChatMessageDTO dto, String userId, String ipAddress) {
        // 内容和图片不能同时为空
        boolean hasContent = StringUtils.isNotBlank(dto.getContent());
        boolean hasImages = validateMessage(dto, hasContent);

        // 敏感词替换
        String content = hasContent ? SensitiveWordUtils.replace(dto.getContent()) : dto.getContent();

        ChatMessageEntity chatMessage = ChatMessageEntity.builder()
                .userId(userId)
                .content(content)
                .images(hasImages ? JSON.toJSONString(dto.getImages()) : null)
                .ipAddress(ipAddress)
                .ipRegion(IpUtils.getIpLocation(ipAddress))
                .build();
        chatMessageMapper.insertMessage(chatMessage);
        return chatMessage;
    }

    private static boolean validateMessage(ChatMessageDTO dto, boolean hasContent) {
        boolean hasImages = dto.getImages() != null && !dto.getImages().isEmpty();
        if (!hasContent && !hasImages) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), ChatConst.MESSAGE_CONTENT_EMPTY);
        }
        // 内容长度校验
        if (hasContent && dto.getContent().length() > ChatConst.MAX_CONTENT_LENGTH) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), ChatConst.CONTENT_TOO_LONG);
        }
        // 图片数量校验
        if (hasImages && dto.getImages().size() > ChatConst.MAX_IMAGE_COUNT) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), ChatConst.TOO_MANY_IMAGES);
        }
        return hasImages;
    }
}
