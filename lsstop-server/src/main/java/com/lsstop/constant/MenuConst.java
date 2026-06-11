package com.lsstop.constant;

/**
 * 菜单模块常量
 *
 * @author lishusheng
 * @date 2026/06/07
 */
public class MenuConst {

    /**
     * 菜单类型无效
     */
    public static final String INVALID_MENU_TYPE = "菜单类型无效";

    /**
     * 父级菜单不存在
     */
    public static final String PARENT_NOT_FOUND = "父级菜单不存在";

    /**
     * 按钮只能挂在菜单下
     */
    public static final String BUTTON_MUST_UNDER_MENU = "按钮权限只能挂在菜单下";

    /**
     * 只能挂在目录下或顶级
     */
    public static final String MUST_UNDER_DIRECTORY = "只能挂在目录下或设为顶级";

    /**
     * 只能挂在目录或菜单下
     */
    public static final String MUST_UNDER_DIRECTORY_OR_MENU = "只能挂在目录或菜单下";

    /**
     * 路由标识已存在
     */
    public static final String NAME_EXISTS = "路由标识已存在";

    /**
     * 同级路由地址重复
     */
    public static final String PATH_EXISTS = "同级路由地址已存在";

    /**
     * 同级权限标识重复
     */
    public static final String AUTH_MARK_EXISTS = "同级权限标识已存在";

    /**
     * path必填
     */
    public static final String PATH_REQUIRED = "路由地址不能为空";

    /**
     * component必填
     */
    public static final String COMPONENT_REQUIRED = "组件路径不能为空";

    /**
     * link必填
     */
    public static final String LINK_REQUIRED = "外部链接不能为空";

    /**
     * link格式错误
     */
    public static final String LINK_INVALID = "外部链接必须以http://或https://开头";

    /**
     * authMark必填
     */
    public static final String AUTH_MARK_REQUIRED = "权限标识不能为空";

    /**
     * 按钮parentId必填
     */
    public static final String BUTTON_PARENT_REQUIRED = "按钮权限必须挂在菜单页面下";

    /**
     * 菜单类型-目录
     */
    public static final int TYPE_DIRECTORY = 1;

    /**
     * 菜单类型-菜单
     */
    public static final int TYPE_MENU = 2;

    /**
     * 菜单类型-按钮
     */
    public static final int TYPE_BUTTON = 3;

    /**
     * 菜单类型-内嵌
     */
    public static final int TYPE_IFRAME = 4;

    /**
     * 菜单类型-外链
     */
    public static final int TYPE_LINK = 5;

    /**
     * 顶级菜单parentId
     */
    public static final int TOP_LEVEL_PARENT_ID = 0;

    /**
     * 排序默认值
     */
    public static final int DEFAULT_SORT = 1;

    /**
     * path最大长度
     */
    public static final int PATH_MAX_LENGTH = 100;

    /**
     * name最大长度
     */
    public static final int NAME_MAX_LENGTH = 50;

    /**
     * component最大长度
     */
    public static final int COMPONENT_MAX_LENGTH = 200;

    /**
     * authMark最大长度
     */
    public static final int AUTH_MARK_MAX_LENGTH = 30;

    /**
     * link最大长度
     */
    public static final int LINK_MAX_LENGTH = 500;

    /**
     * activePath最大长度
     */
    public static final int ACTIVE_PATH_MAX_LENGTH = 100;

    /**
     * icon最大长度
     */
    public static final int ICON_MAX_LENGTH = 100;

    /**
     * path格式错误
     */
    public static final String PATH_FORMAT_INVALID = "路由地址格式不正确，只允许字母、数字、-、_、/、:";

    /**
     * path长度错误
     */
    public static final String PATH_LENGTH_INVALID = "路由地址长度为1~100个字符";

    /**
     * 顶级菜单path必须以/开头
     */
    public static final String PATH_TOP_LEVEL_SLASH = "顶级菜单路由地址必须以/开头";

    /**
     * 非顶级菜单path不能以/开头
     */
    public static final String PATH_SUB_LEVEL_NO_SLASH = "非顶级菜单路由地址不能以/开头";

    /**
     * name格式错误
     */
    public static final String NAME_FORMAT_INVALID = "路由标识必须为PascalCase，以大写字母开头";

    /**
     * component格式错误
     */
    public static final String COMPONENT_FORMAT_INVALID = "组件路径必须以/开头，只允许字母、数字、-、_、/";

    /**
     * icon格式错误
     */
    public static final String ICON_FORMAT_INVALID = "图标格式不正确，需为iconify格式，如 ri:user-line";

    /**
     * authMark格式错误
     */
    public static final String AUTH_MARK_FORMAT_INVALID = "权限标识格式不正确，只允许小写字母、数字、下划线";

    /**
     * authMark长度错误
     */
    public static final String AUTH_MARK_LENGTH_INVALID = "权限标识长度为1~30个字符";

    /**
     * 内嵌菜单path前缀
     */
    public static final String IFRAME_PATH_PREFIX = "/outside/iframe/";

    /**
     * 内嵌菜单path前缀校验错误
     */
    public static final String IFRAME_PATH_PREFIX_INVALID = "内嵌菜单路由地址必须以" + IFRAME_PATH_PREFIX + "开头";

    /**
     * 内嵌菜单path不能仅为前缀，必须包含路径段
     */
    public static final String IFRAME_PATH_EMPTY = "内嵌菜单路由地址不能仅为" + IFRAME_PATH_PREFIX + "，需包含具体路径";

    /**
     * activePath格式错误
     */
    public static final String ACTIVE_PATH_FORMAT_INVALID = "激活路径必须以/开头";

    /**
     * 菜单不存在
     */
    public static final String MENU_NOT_FOUND = "菜单不存在";

    /**
     * 存在子菜单不允许删除
     */
    public static final String HAS_CHILDREN = "该菜单下存在子菜单，请先删除子菜单";

    /**
     * 不允许修改菜单类型
     */
    public static final String MENU_TYPE_IMMUTABLE = "不允许修改菜单类型";

    /**
     * 不允许将菜单移动到自身或其子菜单下
     */
    public static final String CIRCULAR_REFERENCE = "不允许将菜单移动到自身或其子菜单下";

}
