package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.common.Result;
import com.lsstop.domain.dto.MessageDto;
import com.lsstop.domain.vo.MessageVo;
import com.lsstop.service.MessageService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 留言控制层
 *
 * @author lishusheng
 * @date 2025/12/21
 */
@RestController
public class MessageController {

    @Resource
    MessageService messageService;

    /**
     * 前台获取留言列表
     *
     * @return 留言数据
     */
    @AccessLimit(seconds = 60, maxCount = 30)
    @GetMapping("/message/blogListMessage")
    public Result<List<MessageVo>> blogListMessage() {
        return Result.success(messageService.blogListMessage());
    }

    /**
     * 前台新增留言
     *
     * @param messageDto 留言数据
     * @return 响应结果
     */
    @AccessLimit(seconds = 60, maxCount = 30)
    @PostMapping("/message/addMessage")
    public Result<Void> addMessage(@RequestBody @Validated MessageDto messageDto) {
        System.out.println(messageDto);
        return Result.success();
    }

}
