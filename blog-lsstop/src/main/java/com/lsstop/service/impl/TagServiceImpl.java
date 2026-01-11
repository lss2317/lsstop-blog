package com.lsstop.service.impl;

import com.lsstop.domain.vo.TagVO;
import com.lsstop.mapper.TagMapper;
import com.lsstop.service.TagService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 标签服务实现类
 *
 * @author lishusheng
 * @date 2026/01/11
 */
@Service
public class TagServiceImpl implements TagService {

    @Resource
    private TagMapper tagMapper;

    /**
     * 获取标签列表
     *
     * @return 标签列表
     */
    @Override
    public List<TagVO> getTagList() {
        return tagMapper.getTagList();
    }
}
