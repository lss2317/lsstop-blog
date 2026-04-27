package com.lsstop.service.impl;

import com.lsstop.constant.RedisConst;
import com.lsstop.domain.vo.TalkInfoVO;
import com.lsstop.domain.vo.TalkVO;
import com.lsstop.mapper.TalkMapper;
import com.lsstop.service.TalkService;
import com.lsstop.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 说说服务实现类
 *
 * @author lishusheng
 * @date 2026/01/01
 */
@Service
public class TalkServiceImpl implements TalkService {

    @Resource
    private TalkMapper talkMapper;

    @Resource
    private RedisUtils redisUtils;

    /**
     * 获取说说列表
     * <p>查询所有说说数据并转换为视图对象返回</p>
     *
     * @return 说说视图对象列表
     */
    @Override
    public List<TalkVO> listTalk() {
        List<TalkVO> talkVOs = talkMapper.listTalk();
        if (talkVOs.isEmpty()) {
            return talkVOs;
        }
        // 批量构建 Redis key
        List<String> likeKeys = talkVOs.stream()
                .map(vo -> RedisConst.TALK_LIKE_COUNT + vo.getId())
                .collect(Collectors.toList());
        List<String> commentKeys = talkVOs.stream()
                .map(vo -> RedisConst.TALK_COMMENT_COUNT + vo.getId())
                .collect(Collectors.toList());
        // 批量获取点赞数和评论数
        List<Integer> likeCounts = redisUtils.mGet(likeKeys, Integer.class);
        List<Integer> commentCounts = redisUtils.mGet(commentKeys, Integer.class);
        // 设置点赞数和评论数
        for (int i = 0; i < talkVOs.size(); i++) {
            TalkVO vo = talkVOs.get(i);
            vo.setLikeCount(likeCounts.get(i) == null ? 0 : likeCounts.get(i));
            vo.setCommentCount(commentCounts.get(i) == null ? 0 : commentCounts.get(i));
        }
        return talkVOs;
    }

    /**
     * 根据id获取说说详情
     *
     * @param id 说说id
     * @return 说说详情
     */
    @Override
    public TalkInfoVO getTalkById(int id) {
        TalkVO talkVO = talkMapper.getTalkById(id);
        if (talkVO == null) {
            return null;
        }
        // 获取点赞数
        Integer likeCount = redisUtils.get(RedisConst.TALK_LIKE_COUNT + talkVO.getId(), Integer.class);
        talkVO.setLikeCount(likeCount == null ? 0 : likeCount);
        return talkVO.asViewObject(TalkInfoVO.class);
    }
}
