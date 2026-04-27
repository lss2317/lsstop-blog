package com.lsstop.mapper;

import com.lsstop.domain.entity.ChatMessageEntity;
import com.lsstop.domain.vo.ChatMessageVO;

import java.util.List;

/**
 * 聊天消息数据访问层
 *
 * @author lishusheng
 * @date 2026/04/04
 */
public interface ChatMessageMapper {

    /**
     * 获取聊天消息列表（游标分页）
     *
     * @param lastId 上一页最后一条消息的id，首次传null
     * @param size   每页大小
     * @return 聊天消息列表
     */
    List<ChatMessageVO> listMessage(Integer lastId, Integer size);

    /**
     * 新增聊天消息
     *
     * @param chatMessage 聊天消息实体
     */
    void insertMessage(ChatMessageEntity chatMessage);
}
