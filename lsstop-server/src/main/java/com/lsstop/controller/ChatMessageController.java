package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.constant.ChatConst;
import com.lsstop.common.Result;
import com.lsstop.domain.vo.ChatMessageVO;
import com.lsstop.enums.StatusEnum;
import com.lsstop.exception.BusinessException;
import com.lsstop.service.ChatMessageService;
import jakarta.annotation.Resource;
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
}
