package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.common.Result;
import com.lsstop.domain.vo.TalkVo;
import com.lsstop.service.TalkService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 说说控制器
 *
 * @author lishusheng
 * @date 2026/01/01
 */
@RestController
@RequestMapping("/talk")
public class TalkController {

    @Resource
    private TalkService talkService;

    /**
     * 获取说说列表
     *
     * @return 说说列表
     */
    @RequestMapping("/listTalk")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<List<TalkVo>> listTalk() {
        return Result.success(talkService.listTalk());
    }
}
