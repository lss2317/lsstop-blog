package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.common.Result;
import com.lsstop.constant.CommonConst;
import com.lsstop.domain.dto.MessageDTO;
import com.lsstop.domain.entity.MessageEntity;
import com.lsstop.domain.vo.AddMessageVO;
import com.lsstop.domain.vo.MessageVO;
import com.lsstop.service.MessageService;
import com.lsstop.service.WebsiteConfigService;
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
public class MessageController {

    @Resource
    MessageService messageService;

    @Resource
    WebsiteConfigService websiteConfigService;

    /**
     * 前台新增留言
     *
     * @param messageDTO 留言数据
     * @return 响应结果
     */
    @PostMapping("/front/message/addMessage")
    @AccessLimit(seconds = 60, maxCount = 30)
    public Result<AddMessageVO> addMessage(@RequestBody @Validated MessageDTO messageDTO, HttpServletRequest request) {
        MessageEntity message = messageDTO.asViewObject(MessageEntity.class);
        message.setIpAddress(IpUtils.getIpAddress(request));
        message.setIpRegion(IpUtils.getIpLocation(message.getIpAddress()));
        message.setReview(websiteConfigService.getWebsiteConfig().getEnableMessageReview());
        MessageEntity savedMessage = messageService.insertMessage(message);
        return Result.success(savedMessage.asViewObject(AddMessageVO.class));
    }

    /**
     * 前台获取留言列表
     *
     * @return 留言数据
     */
    @GetMapping("/front/message/listMessage")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<List<MessageVO>> getMessageList() {
        List<MessageVO> messageVOList = messageService.getMessageList().stream()
                .map(message -> message.asViewObject(MessageVO.class))
                .toList();
        return Result.success(messageVOList);
    }

}
