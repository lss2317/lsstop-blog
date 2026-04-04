package com.lsstop.service.impl;

import com.alibaba.fastjson2.JSON;
import com.lsstop.domain.entity.ChatMessageEntity;
import com.lsstop.domain.vo.ChatMessageVO;
import com.lsstop.mapper.ChatMessageMapper;
import com.lsstop.service.ChatMessageService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

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
        return list;
    }

    /**
     * 新增聊天消息
     *
     * @param chatMessage 聊天消息实体
     * @return 新增的消息实体（含id）
     */
    @Override
    public ChatMessageEntity insertMessage(ChatMessageEntity chatMessage) {
        chatMessageMapper.insertMessage(chatMessage);
        return chatMessage;
    }
}
