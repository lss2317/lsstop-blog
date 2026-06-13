package com.lsstop.constant;

/**
 * 接口权限模块常量
 *
 * @author lishusheng
 * @date 2026/06/13
 */
public class ApiPermissionConst {

    /**
     * 顶级parentId
     */
    public static final int TOP_LEVEL_PARENT_ID = 0;

    /**
     * 排序默认值
     */
    public static final int DEFAULT_SORT = 1;

    /**
     * 父级权限不存在
     */
    public static final String PARENT_NOT_FOUND = "父级权限不存在";

    /**
     * 权限描述不能为空
     */
    public static final String DESCRIPTION_REQUIRED = "权限描述不能为空";

    /**
     * 请求方法不能为空
     */
    public static final String REQUEST_METHOD_REQUIRED = "请求方法不能为空";

    /**
     * 接口路径不能为空
     */
    public static final String REQUEST_URL_REQUIRED = "接口路径不能为空";

    /**
     * 接口权限不存在
     */
    public static final String API_PERMISSION_NOT_FOUND = "接口权限不存在";

    /**
     * 存在子级权限
     */
    public static final String HAS_CHILDREN = "存在子级权限，请先删除子级";

    /**
     * 循环引用
     */
    public static final String CIRCULAR_REFERENCE = "不能将权限移动到自身或其子级下";

    /**
     * 请求方法无效
     */
    public static final String INVALID_REQUEST_METHOD = "请求方法无效，仅支持GET、POST、PUT、DELETE";

    /**
     * 接口路径格式错误
     */
    public static final String REQUEST_URL_FORMAT_INVALID = "接口路径格式不正确，只允许字母、数字、-、_、/、:、*、.";

    /**
     * 目录与接口类型不允许互转
     */
    public static final String TYPE_IMMUTABLE = "目录与接口类型不允许互转";

    /**
     * 接口路径必须以/开头
     */
    public static final String REQUEST_URL_MUST_START_WITH_SLASH = "接口路径必须以/开头";

    /**
     * 相同接口路径与请求方法的权限已存在（全局唯一）
     */
    public static final String SAME_LEVEL_REQUEST_URL_EXISTS = "相同接口路径与请求方法的权限已存在";

    /**
     * 目录节点只能挂在目录下或设为顶级
     */
    public static final String DIRECTORY_MUST_UNDER_DIRECTORY = "目录节点只能挂在其他目录下或设为顶级";

    /**
     * 接口节点只能挂在目录下或设为顶级
     */
    public static final String API_MUST_UNDER_DIRECTORY = "接口节点只能挂在目录下或设为顶级";

}
