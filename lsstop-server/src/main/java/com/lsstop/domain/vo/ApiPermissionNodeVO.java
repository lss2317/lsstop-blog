package com.lsstop.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

/**
 * 接口权限树节点VO
 *
 * @author lishusheng
 * @date 2026/06/04
 */
@Data
public class ApiPermissionNodeVO {

    /**
     * 接口ID
     */
    private Integer id;

    /**
     * 父级权限ID（0表示顶级）
     */
    @JsonIgnore
    private Integer parentId;

    /**
     * 接口名称
     */
    private String name;

    /**
     * 接口路径（目录节点为null）
     */
    private String path;

    /**
     * HTTP方法（目录节点为null）
     */
    private String method;

    /**
     * 子节点
     */
    private List<ApiPermissionNodeVO> children;

}
