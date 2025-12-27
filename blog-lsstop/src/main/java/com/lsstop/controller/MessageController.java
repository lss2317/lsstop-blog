package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.common.Result;
import com.lsstop.domain.dto.MessageDto;
import com.lsstop.domain.entity.Message;
import com.lsstop.domain.vo.MessageVo;
import com.lsstop.service.MessageService;
import com.lsstop.utils.IpUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 留言控制层
 *
 * @author lishusheng
 * @date 2025/12/21
 */
@RestController
@RequestMapping("/message")
public class MessageController {

    @Resource
    MessageService messageService;

    /**
     * 前台新增留言
     *
     * @param messageDto 留言数据
     * @return 响应结果
     */
    @PostMapping("/addMessage")
    @AccessLimit(seconds = 60, maxCount = 30)
    public Result<Void> addMessage(@RequestBody @Validated MessageDto messageDto, HttpServletRequest request) {
        Message message = messageDto.asViewObject(Message.class);
        message.setIpAddress(IpUtils.getIpAddress(request));
        message.setIpSource(IpUtils.getIpSource(message.getIpAddress()));
        messageService.insertMessage(message);
        return Result.success();
    }

    /**
     * 前台获取留言列表
     *
     * @return 留言数据
     */
    @GetMapping("/listMessage")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<List<MessageVo>> getMessageList() {
        List<MessageVo> messageVoList = messageService.getMessageList().stream()
                .map(message -> message.asViewObject(MessageVo.class))
                .toList();
        return Result.success(messageVoList);
    }

}
