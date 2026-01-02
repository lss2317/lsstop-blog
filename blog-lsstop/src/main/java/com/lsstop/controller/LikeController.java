package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.common.Result;
import com.lsstop.domain.dto.LikeDTO;
import com.lsstop.domain.vo.UserLikeVO;
import com.lsstop.enums.LikeTypeEnum;
import com.lsstop.service.LikeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 点赞控制器
 *
 * @author lishusheng
 * @date 2026/01/02
 */
@RestController
@RequestMapping("/like")
public class LikeController {

    @Resource
    private LikeService likeService;

    /**
     * 点赞/取消点赞
     *
     * @param likeDTO 点赞请求参数
     * @return 点赞状态，true表示已点赞，false表示已取消
     */
    @PostMapping("/toggle")
    @AccessLimit(seconds = 1, maxCount = 5)
    public Result<Boolean> toggleLike(@RequestBody LikeDTO likeDTO) {
        LikeTypeEnum likeType = LikeTypeEnum.of(likeDTO.getType());
        if (likeType == null) {
            return Result.failure("无效的点赞类型");
        }
        boolean isLiked = likeService.toggleLike(likeDTO.getUserId(), likeDTO.getTargetId(), likeType);
        return Result.success(isLiked);
    }

    /**
     * 获取用户所有点赞信息
     *
     * @param userId 用户id
     * @return 用户点赞信息
     */
    @GetMapping("/userLike/{userId}")
    @AccessLimit(seconds = 10, maxCount = 10)
    public Result<UserLikeVO> getUserLikes(@PathVariable String userId) {
        UserLikeVO userLikeVO = likeService.getUserLikes(userId);
        return Result.success(userLikeVO);
    }

}
