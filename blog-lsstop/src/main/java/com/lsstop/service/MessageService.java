package com.lsstop.service;

import com.lsstop.domain.entity.Message;

import java.util.List;

/**
 * 留言服务接口
 *
 * @author lishusheng
 * @date 2025/12/21
 */
public interface MessageService {

    /**
     * 前台获取留言数据
     *
     * @return 留言列表
     */
    List<Message> blogListMessage();

    /**
     * 新增留言
     *
     * @param message 留言实体
     */
    void insertMessage(Message message);
}
