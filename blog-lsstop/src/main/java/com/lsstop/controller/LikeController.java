package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.common.Result;
import com.lsstop.domain.dto.LikeDTO;
import com.lsstop.domain.vo.UserLikeVO;
import com.lsstop.enums.LikeTypeEnum;
import com.lsstop.enums.StatusEnum;
import com.lsstop.exception.BusinessException;
import com.lsstop.service.LikeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 点赞控制器
 *
 * @author lishusheng
 * @date 2026/01/02
 */
@RestController
public class LikeController {

    @Resource
    private LikeService likeService;

    /**
     * 点赞/取消点赞
     *
     * @param likeDTO 点赞请求参数
     * @param request 请求对象（拦截器已验证token并存入userId）
     * @return 点赞状态，true表示已点赞，false表示已取消
     */
    @PostMapping("/front/like/toggle")
    @AccessLimit(seconds = 1, maxCount = 5)
    public Result<Boolean> toggleLike(@RequestBody @Validated LikeDTO likeDTO, HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        if (userId == null || userId.isBlank()) {
            throw new BusinessException(StatusEnum.NOT_LOGIN);
        }
        LikeTypeEnum likeType = LikeTypeEnum.of(likeDTO.getType());
        if (likeType == null) {
            return Result.failure("无效的点赞类型");
        }
        boolean isLiked = likeService.toggleLike(userId, likeDTO.getTargetId(), likeType);
        return Result.success(isLiked);
    }

    /**
     * 获取用户所有点赞信息
     *
     * @param userId 用户id
     * @return 用户点赞信息
     */
    @GetMapping("/front/like/userLike/{userId}")
    @AccessLimit(seconds = 10, maxCount = 10)
    public Result<UserLikeVO> getUserLikes(@PathVariable String userId) {
        UserLikeVO userLikeVO = likeService.getUserLikes(userId);
        return Result.success(userLikeVO);
    }

}
