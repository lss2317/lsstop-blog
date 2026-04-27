package com.lsstop.service;

import com.lsstop.domain.dto.ChatMessageDTO;
import com.lsstop.domain.entity.ChatMessageEntity;
import com.lsstop.domain.vo.ChatMessageVO;

import java.util.List;

/**
 * 聊天消息服务
 *
 * @author lishusheng
 * @date 2026/04/04
 */
public interface ChatMessageService {

    /**
     * 获取聊天消息列表（游标分页）
     *
     * @param lastId 上一页最后一条消息的id，首次传null
     * @param size   每页大小
     * @return 聊天消息列表
     */
    List<ChatMessageVO> listMessage(Integer lastId, Integer size);

    /**
     * 发送聊天消息（含参数校验、构建实体、持久化）
     *
     * @param dto       聊天消息请求参数
     * @param userId    用户id
     * @param ipAddress 用户IP地址
     * @return 新增的消息实体（含id）
     */
    ChatMessageEntity sendMessage(ChatMessageDTO dto, String userId, String ipAddress);
}
