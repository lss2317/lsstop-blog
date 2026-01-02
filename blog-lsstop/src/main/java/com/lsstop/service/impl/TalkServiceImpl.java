package com.lsstop.service.impl;

import com.lsstop.constant.RedisConst;
import com.lsstop.domain.vo.TalkInfoVo;
import com.lsstop.domain.vo.TalkVo;
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
    public List<TalkVo> listTalk() {
        List<TalkVo> talkVos = talkMapper.listTalk();
        if (talkVos.isEmpty()) {
            return talkVos;
        }
        // 批量构建 Redis key
        List<String> likeKeys = talkVos.stream()
                .map(vo -> RedisConst.TALK_LIKE_COUNT + vo.getId())
                .collect(Collectors.toList());
        List<String> commentKeys = talkVos.stream()
                .map(vo -> RedisConst.TALK_COMMENT_COUNT + vo.getId())
                .collect(Collectors.toList());
        // 批量获取点赞数和评论数
        List<Integer> likeCounts = redisUtils.mGet(likeKeys, Integer.class);
        List<Integer> commentCounts = redisUtils.mGet(commentKeys, Integer.class);
        // 设置点赞数和评论数
        for (int i = 0; i < talkVos.size(); i++) {
            TalkVo vo = talkVos.get(i);
            vo.setLikeCount(likeCounts.get(i) == null ? 0 : likeCounts.get(i));
            vo.setCommentCount(commentCounts.get(i) == null ? 0 : commentCounts.get(i));
        }
        return talkVos;
    }

    /**
     * 根据id获取说说详情
     *
     * @param id 说说id
     * @return 说说详情
     */
    @Override
    public TalkInfoVo getTalkById(int id) {
        TalkVo talkVo = talkMapper.getTalkById(id);
        if (talkVo == null) {
            return null;
        }
        // 获取点赞数
        Integer likeCount = redisUtils.get(RedisConst.TALK_LIKE_COUNT + talkVo.getId(), Integer.class);
        talkVo.setLikeCount(likeCount == null ? 0 : likeCount);
        return talkVo.asViewObject(TalkInfoVo.class);
    }
}
