package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.common.Result;
import com.lsstop.constant.CommonConst;
import com.lsstop.domain.dto.CommentDTO;
import com.lsstop.domain.entity.CommentEntity;
import com.lsstop.domain.vo.AddCommentVO;
import com.lsstop.domain.vo.CommentPageVO;
import com.lsstop.domain.vo.CommentReplyVO;
import com.lsstop.domain.vo.CommentVO;
import com.lsstop.enums.CommentTypeEnum;
import com.lsstop.enums.StatusEnum;
import com.lsstop.exception.BusinessException;
import com.lsstop.service.CommentService;
import com.lsstop.service.WebsiteConfigService;
import com.lsstop.utils.IpUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Resource
    private WebsiteConfigService websiteConfigService;

    /**
     * 新增评论
     *
     * @param commentDTO 评论请求参数
     * @param request    请求对象（拦截器已验证token并存入userId）
     * @return 新增的评论信息
     */
    @PostMapping("/front/comment/addComment")
    @AccessLimit(seconds = 60, maxCount = 30)
    public Result<AddCommentVO> addComment(@RequestBody @Validated CommentDTO commentDTO, HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        if (userId == null || userId.isBlank()) {
            throw new BusinessException(StatusEnum.NOT_LOGIN);
        }
        CommentTypeEnum commentType = CommentTypeEnum.of(commentDTO.getTargetType());
        if (commentType == null) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), CommonConst.INVALID_COMMENT_TYPE);
        }
        // 友链类型时targetId默认0
        if (commentType == CommentTypeEnum.FRIEND_LINK) {
            commentDTO.setTargetId(CommonConst.FRIEND_LINK_DEFAULT_TARGET_ID);
        }
        CommentEntity comment = commentDTO.asViewObject(CommentEntity.class);
        comment.setUserId(userId);
        comment.setIpRegion(IpUtils.getIpLocation(IpUtils.getIpAddress(request)));
        comment.setReview(websiteConfigService.getWebsiteConfig().getEnableCommentReview());
        AddCommentVO vo = commentService.insertComment(comment);
        return Result.success(vo);
    }

    /**
     * 获取评论列表（分页）
     *
     * @param type     评论类型：1=文章, 2=友链, 3=说说
     * @param typeId   对应内容的ID（友链时可不传，默认0）
     * @param current  当前页码，默认1
     * @param sortType 排序方式：hot=最热, new=最新
     * @return 评论列表及总数
     */
    @GetMapping("/front/comment/listComment")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<CommentPageVO> listComment(@RequestParam Integer type,
                                             @RequestParam(required = false) Integer typeId,
                                             @RequestParam(defaultValue = "1") Integer current,
                                             @RequestParam(defaultValue = "new") String sortType) {
        // 参数校验
        CommentTypeEnum commentType = CommentTypeEnum.of(type);
        if (commentType == null) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), CommonConst.INVALID_COMMENT_TYPE);
        }
        if (current < 1) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), CommonConst.INVALID_PAGE_PARAM);
        }
        if (!CommonConst.SORT_TYPE_HOT.equals(sortType) && !CommonConst.SORT_TYPE_NEW.equals(sortType)) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), CommonConst.INVALID_SORT_TYPE);
        }
        // 友链类型时typeId默认0，其他类型必传且大于0
        if (type.equals(CommentTypeEnum.FRIEND_LINK.getType())) {
            typeId = CommonConst.FRIEND_LINK_DEFAULT_TARGET_ID;
        } else if (typeId == null || typeId < 1) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), CommonConst.TARGET_ID_REQUIRED);
        }
        List<CommentVO> commentList = commentService.getCommentList(typeId, type, current, CommonConst.DEFAULT_PAGE_SIZE, sortType);
        Integer total = commentService.getCommentCount(typeId, type);
        return Result.success(new CommentPageVO(commentList, total));
    }

    /**
     * 获取子评论列表（分页）
     *
     * @param parentId 父评论id
     * @param current  当前页码，默认1
     * @param sortType 排序方式：hot=最热, new=最新
     * @return 子评论列表
     */
    @GetMapping("/front/comment/listReply")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<List<CommentReplyVO>> listReply(@RequestParam Integer parentId,
                                                  @RequestParam(defaultValue = "1") Integer current,
                                                  @RequestParam(defaultValue = "new") String sortType) {
        // 参数校验
        if (parentId == null || parentId < 1) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), CommonConst.PARENT_ID_REQUIRED);
        }
        if (current < 1) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), CommonConst.INVALID_PAGE_PARAM);
        }
        if (!CommonConst.SORT_TYPE_HOT.equals(sortType) && !CommonConst.SORT_TYPE_NEW.equals(sortType)) {
            throw new BusinessException(StatusEnum.PARAM_ERROR.getCode(), CommonConst.INVALID_SORT_TYPE);
        }
        List<CommentReplyVO> replyList = commentService.getReplyList(parentId, current, CommonConst.DEFAULT_CHILD_COMMENT_LIMIT, sortType);
        return Result.success(replyList);
    }
}
