package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.common.Result;
import com.lsstop.domain.entity.FriendLink;
import com.lsstop.domain.vo.FriendLinkVo;
import com.lsstop.service.FriendLinkService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 友链控制层
 *
 * @author lishusheng
 * @date 2025/12/27
 */
@RestController
@RequestMapping("friendLink")
public class FriendLinkController {

    @Resource
    private FriendLinkService friendLinkService;

    /**
     * 获取友链列表
     *
     * @return 友链列表
     */
    @GetMapping("/listFriendLink")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<List<FriendLinkVo>> getFriendLinkList() {
        List<FriendLink> friendLinkList = friendLinkService.getFriendLinkList();
        List<FriendLinkVo> list = friendLinkList.stream().map(friendLink -> friendLink.asViewObject(FriendLinkVo.class)).toList();
        return Result.success(list);
    }

}
