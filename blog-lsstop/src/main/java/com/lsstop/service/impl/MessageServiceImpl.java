package com.lsstop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsstop.domain.entity.Message;
import com.lsstop.domain.vo.MessageVo;
import com.lsstop.mapper.MessageMapper;
import com.lsstop.service.MessageService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lishusheng
 * @date 2025/12/21
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Resource
    private MessageMapper messageMapper;

    @Override
    public List<MessageVo> blogListMessage() {
        List<Message> messageList = messageMapper.blogListMessage();
        return messageList.stream().map(message -> message.asViewObject(MessageVo.class)).toList();
    }
}
