package com.lsstop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsstop.domain.entity.Message;

import java.util.List;

/**
 * @author lishusheng
 * @date 2025/12/21
 */
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 获取不需要审核留言总数据给前台
     *
     * @return 留言List集合
     */
    List<Message> blogListMessage();
}
