package com.lsstop.controller;

import com.lsstop.common.Result;
import com.lsstop.domain.vo.PageManagementVo;
import com.lsstop.service.PageManagementService;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/pageManagement")
@RequiredArgsConstructor
public class PageManagementController {

    private final PageManagementService pageManagementService;

    /**
     * 获取页面列表
     *
     * @return 页面信息列表
     */
    @GetMapping("/listPageManagement")
    public Result<List<PageManagementVo>> listAllPageManagement() {
        List<PageManagementVo> pageManagementVoList = pageManagementService.listAllPageManagement().stream()
                .map(pageManagement -> pageManagement.asViewObject(PageManagementVo.class))
                .toList();
        return Result.success(pageManagementVoList);
    }
}
