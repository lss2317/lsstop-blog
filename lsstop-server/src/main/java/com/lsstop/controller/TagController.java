package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.common.Result;
import com.lsstop.domain.vo.TagVO;
import com.lsstop.service.TagService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 标签控制层
 *
 * @author lishusheng
 * @date 2026/01/11
 */
@RestController
public class TagController {

    @Resource
    private TagService tagService;

    /**
     * 获取标签列表
     *
     * @return 标签列表
     */
    @GetMapping("/front/tag/listTag")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<List<TagVO>> getTagList() {
        return Result.success(tagService.getTagList());
    }

}
