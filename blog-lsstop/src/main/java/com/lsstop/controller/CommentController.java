package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.common.Result;
import com.lsstop.constant.CommonConst;
import com.lsstop.domain.dto.CommentDTO;
import com.lsstop.domain.entity.CommentEntity;
import com.lsstop.enums.CommentTypeEnum;
import com.lsstop.enums.StatusEnum;
import com.lsstop.exception.BusinessException;
import com.lsstop.service.CommentService;
import com.lsstop.utils.IpUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 评论控制器
 *
 * @author lishusheng
 * @date 2026/01/28
 */
@RestController
public class CommentController {

    @Resource
    private CommentService commentService;

    /**
     * 新增评论
     *
     * @param commentDTO 评论请求参数
     * @param request    请求对象（拦截器已验证token并存入userId）
     * @return 响应结果
     */
    @PostMapping("/front/comment/addComment")
    @AccessLimit(seconds = 60, maxCount = 30)
    public Result<Void> addComment(@RequestBody @Validated CommentDTO commentDTO, HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        if (userId == null || userId.isBlank()) {
            throw new BusinessException(StatusEnum.NOT_LOGIN);
        }
        CommentTypeEnum commentType = CommentTypeEnum.of(commentDTO.getTargetType());
        if (commentType == null) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), CommonConst.INVALID_COMMENT_TYPE);
        }
        CommentEntity comment = commentDTO.asViewObject(CommentEntity.class);
        comment.setUserId(userId);
        comment.setIpRegion(IpUtils.getIpLocation(IpUtils.getIpAddress(request)));
        comment.setStatus(CommonConst.STATUS_NORMAL);
        commentService.insertComment(comment);
        return Result.success();
    }
}
