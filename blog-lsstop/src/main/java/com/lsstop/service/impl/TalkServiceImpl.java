package com.lsstop.service.impl;

import com.lsstop.domain.vo.TalkVo;
import com.lsstop.mapper.TalkMapper;
import com.lsstop.service.TalkService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 说说服务实现类
 *
 * @author lishusheng
 * @date 2026/01/01
 */
@Service
public class TalkServiceImpl implements TalkService {

    /**
     * 说说数据访问层
     */
    @Resource
    private TalkMapper talkMapper;

    /**
     * 获取说说列表
     * <p>查询所有说说数据并转换为视图对象返回</p>
     *
     * @return 说说视图对象列表
     */
    @Override
    public List<TalkVo> listTalk() {
        //TODO 点赞数、评论数
        List<TalkVo> talkVos = talkMapper.listTalk();
        return talkVos.stream().peek(talkVo -> {
            talkVo.setLikeCount(10);
            talkVo.setCommentCount(20);
        }).toList();
    }

    /**
     * 根据id获取说说
     *
     * @param id 说说id
     * @return 说说视图对象
     */
    @Override
    public TalkVo getTalkById(int id) {
        TalkVo talkVo = talkMapper.getTalkById(id);
        if (talkVo == null) {
            return null;
        }
        talkVo.setLikeCount(300);
        return talkVo;
    }
}
