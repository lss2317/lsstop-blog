package com.lsstop.service.impl;

import com.lsstop.domain.entity.Talk;
import com.lsstop.domain.vo.TalkVo;
import com.lsstop.mapper.TalkMapper;
import com.lsstop.service.TalkService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        //TODO 用户头像、点赞数、评论数
        List<String> imgList = new ArrayList<>();
        return talkMapper.listTalk().stream()
                .map(talk -> talk.asViewObject(TalkVo.class))
                .peek(talkVo -> {
                    talkVo.setAvatar("https://blog-1307541812.cos.ap-shanghai.myqcloud.com/8a21f0f6-b221-4ef1-8e29-6d4fb321df1b.jpeg");
                    talkVo.setNickname("阿圣");
                    talkVo.setLikeCount(10);
                    talkVo.setCommentCount(20);
                    talkVo.setImgList(imgList);
                })
                .toList();
    }
}
