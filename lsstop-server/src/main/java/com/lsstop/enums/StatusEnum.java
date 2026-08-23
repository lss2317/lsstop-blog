package com.lsstop.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * HTTP状态码枚举
 *
 * @author lishusheng
 */
@Getter
public enum StatusEnum {

    /**
     * 请求成功
     */
    SUCCESS(200, "success", HttpStatus.OK),

    /**
     * 请求失败
     */
    FAILURE(500, "failure", HttpStatus.INTERNAL_SERVER_ERROR),

    /**
     * 登录已过期（token无效或过期，前端可刷新重试）
     */
    TOKEN_EXPIRED(40102, "登录已过期", HttpStatus.UNAUTHORIZED),

    /**
     * 用户名或密码错误
     */
    USERNAME_OR_PASSWORD_ERROR(401, "用户名或密码错误", HttpStatus.UNAUTHORIZED),

    /**
     * 未登录提示
     */
    NOT_LOGIN(401, "请先登录", HttpStatus.UNAUTHORIZED),

    /**
     * 操作权限不足
     */
    NO_PERMISSION(403, "当前账号暂无此操作权限，如需使用请联系管理员", HttpStatus.FORBIDDEN),

    /**
     * 请求频繁
     */
    REQUEST_FREQUENTLY(429, "请求频繁", HttpStatus.TOO_MANY_REQUESTS),

    /**
     * 验证码错误
     */
    VERIFY_CODE_ERROR(400, "验证码错误", HttpStatus.BAD_REQUEST),

    /**
     * 用户名或邮箱已存在
     */
    USERNAME_OR_EMAIL_EXIST(409, "用户名或邮箱已存在", HttpStatus.CONFLICT),

    /**
     * 参数错误提示
     */
    PARAM_ERROR(400, "参数错误", HttpStatus.BAD_REQUEST),

    /**
     * 系统异常
     */
    OTHER_ERROR(500, "系统异常", HttpStatus.INTERNAL_SERVER_ERROR),

    /**
     * 会话数量已达上限
     */
    SESSION_LIMIT(429, "会话数量已达上限", HttpStatus.TOO_MANY_REQUESTS),

    /**
     * 未删除子菜单
     */
    NO_DELETE_CHILD_MENU(409, "请先删除子菜单", HttpStatus.CONFLICT),

    /**
     * 文件上传错误
     */
    FILE_UPLOAD_ERROR(500, "文件上传错误", HttpStatus.INTERNAL_SERVER_ERROR),

    /**
     * 账号被封禁
     */
    BLACK_LIST_ERROR(403, "账号被封禁", HttpStatus.FORBIDDEN),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在", HttpStatus.NOT_FOUND);

    /**
     * 业务码
     */
    private final Integer code;

    /**
     * 错误信息
     */
    private final String msg;

    /**
     * HTTP状态码
     */
    private final HttpStatus httpStatus;

    StatusEnum(Integer code, String msg, HttpStatus httpStatus) {
        this.code = code;
        this.msg = msg;
        this.httpStatus = httpStatus;
    }
}
