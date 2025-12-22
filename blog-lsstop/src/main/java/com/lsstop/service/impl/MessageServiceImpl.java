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
 * 留言服务实现类
 *
 * @author lishusheng
 * @date 2025/12/21
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Resource
    private MessageMapper messageMapper;

    /**
     * 前台获取留言数据
     *
     * @return 留言VO列表
     */
    @Override
    public List<MessageVo> blogListMessage() {
        List<Message> messageList = messageMapper.blogListMessage();
        return messageList.stream().map(message -> message.asViewObject(MessageVo.class)).toList();
    }

    /**
     * 新增留言
     *
     * @param message 留言实体
     */
    @Override
    public void insertMessage(Message message) {
        messageMapper.insertMessage(message);
    }
}
