package com.lsstop.controller;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.common.Result;
import com.lsstop.domain.vo.PageInfoVo;
import com.lsstop.service.PageInfoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 页面信息控制层
 *
 * @author lishusheng
 * @date 2025/12/24
 */
@RestController
@RequestMapping("/pageInfo")
public class PageInfoController {

    @Resource
    private PageInfoService pageInfoService;

    /**
     * 获取页面列表
     *
     * @return 页面信息列表
     */
    @GetMapping("/listPageInfo")
    @AccessLimit(seconds = 60, maxCount = 60)
    public Result<List<PageInfoVo>> listAllPageInfo() {
        List<PageInfoVo> pageManagementVoList = pageInfoService.listAllPageInfo().stream()
                .map(pageManagement -> pageManagement.asViewObject(PageInfoVo.class))
                .toList();
        return Result.success(pageManagementVoList);
    }
}
