package com.lsstop.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * HTTP状态码枚举
 *
 * @author lishusheng
 */
@Getter
@AllArgsConstructor
public enum StatusEnum {

    /**
     * 请求成功
     */
    SUCCESS(200, "success"),
    /**
     * 请求失败
     */
    FAILURE(500, "failure"),
    /**
     * 用户名或密码错误
     */
    USERNAME_OR_PASSWORD_ERROR(401, "用户名或密码错误"),
    /**
     * 未登录提示
     */
    NOT_LOGIN(401, "请先登录"),
    /**
     * 没有权限
     */
    NO_PERMISSION(403, "没有权限"),
    /**
     * 请求频繁
     */
    REQUEST_FREQUENTLY(429, "请求频繁"),
    /**
     * 验证码错误
     */
    VERIFY_CODE_ERROR(400, "验证码错误"),
    /**
     * 用户名或邮箱已存在
     */
    USERNAME_OR_EMAIL_EXIST(409, "用户名或邮箱已存在"),
    /**
     * 参数错误提示
     */
    PARAM_ERROR(400, "参数错误"),
    /**
     * 系统异常
     */
    OTHER_ERROR(500, "系统异常"),
    /**
     * 会话数量已达上限
     */
    SESSION_LIMIT(429, "会话数量已达上限"),
    /**
     * 未删除子菜单
     */
    NO_DELETE_CHILD_MENU(400, "请先删除子菜单"),
    /**
     * 文件上传错误
     */
    FILE_UPLOAD_ERROR(400, "文件上传错误"),
    /**
     * 账号被封禁
     */
    BLACK_LIST_ERROR(403, "账号被封禁");


    /**
     * code
     */
    private final Integer code;

    /**
     * Msg
     */
    private final String msg;
}
