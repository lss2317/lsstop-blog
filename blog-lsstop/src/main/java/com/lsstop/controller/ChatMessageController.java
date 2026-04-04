package com.lsstop.controller;

import com.alibaba.fastjson2.JSON;
import com.lsstop.annotation.AccessLimit;
import com.lsstop.constant.ChatConst;
import com.lsstop.common.Result;
import com.lsstop.domain.dto.ChatMessageDTO;
import com.lsstop.domain.entity.ChatMessageEntity;
import com.lsstop.domain.vo.ChatMessageVO;
import com.lsstop.enums.StatusEnum;
import com.lsstop.exception.BusinessException;
import com.lsstop.service.ChatMessageService;
import com.lsstop.utils.IpUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 聊天消息控制器
 *
 * @author lishusheng
 * @date 2026/04/04
 */
@RestController
public class ChatMessageController {

    @Resource
    private ChatMessageService chatMessageService;

    /**
     * 获取聊天消息列表（游标分页）
     *
     * @param lastId 上一页最后一条消息的id，首次加载不传
     * @return 聊天消息列表
     */
    @GetMapping("/front/chat/listMessage")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<List<ChatMessageVO>> listMessage(@RequestParam(required = false) Integer lastId) {
        if (lastId != null && lastId < 1) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), ChatConst.INVALID_LAST_ID);
        }
        List<ChatMessageVO> list = chatMessageService.listMessage(lastId, ChatConst.DEFAULT_PAGE_SIZE);
        return Result.success(list);
    }

    /**
     * 发送聊天消息
     *
     * @param chatMessageDTO 聊天消息请求参数
     * @param request        请求对象（拦截器已验证token并存入userId）
     * @return 响应结果
     */
    @PostMapping("/front/chat/sendMessage")
    @AccessLimit(seconds = 60, maxCount = 30, msg = "发送消息过于频繁")
    public Result<Void> sendMessage(@RequestBody @Validated ChatMessageDTO chatMessageDTO,
                                    HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        if (userId == null || userId.isBlank()) {
            throw new BusinessException(StatusEnum.NOT_LOGIN);
        }
        // 内容和图片不能同时为空
        boolean hasContent = StringUtils.isNotBlank(chatMessageDTO.getContent());
        boolean hasImages = chatMessageDTO.getImages() != null && !chatMessageDTO.getImages().isEmpty();
        if (!hasContent && !hasImages) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), ChatConst.MESSAGE_CONTENT_EMPTY);
        }

        ChatMessageEntity chatMessage = ChatMessageEntity.builder()
                .userId(userId)
                .content(chatMessageDTO.getContent())
                .images(hasImages ? JSON.toJSONString(chatMessageDTO.getImages()) : null)
                .ipAddress(IpUtils.getIpAddress(request))
                .ipRegion(IpUtils.getIpLocation(IpUtils.getIpAddress(request)))
                .build();
        chatMessageService.insertMessage(chatMessage);
        return Result.success();
    }
}
