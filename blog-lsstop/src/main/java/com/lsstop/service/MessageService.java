package com.lsstop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsstop.domain.entity.Message;
import com.lsstop.domain.vo.MessageVo;

import java.util.List;

/**
 * @author lishusheng
 * @date 2025/12/21
 */
public interface MessageService extends IService<Message> {

    /**
     * 前台获取留言数据
     */
    List<MessageVo> blogListMessage();
}
