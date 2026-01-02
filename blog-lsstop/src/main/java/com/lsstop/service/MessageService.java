package com.lsstop.service;

import com.lsstop.domain.dataObject.MessageDO;

import java.util.List;

/**
 * 留言服务
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
    List<MessageDO> getMessageList();

    /**
     * 新增留言
     *
     * @param message 留言实体
     */
    void insertMessage(MessageDO message);
}
