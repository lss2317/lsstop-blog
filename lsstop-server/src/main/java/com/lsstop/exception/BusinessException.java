package com.lsstop.exception;

import com.lsstop.enums.StatusEnum;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serial;

/**
 * 业务异常
 * <p>
 * 用于抛出业务层面的错误，如"用户不存在"、"密码错误"等
 * 支持自定义业务码和HTTP状态码
 * </p>
 *
 * @author lishusheng
 * @date 2026/01/04
 */
@Getter
public class BusinessException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -5377616018356584300L;

    /**
     * 业务状态码
     */
    private final Integer code;

    /**
     * HTTP状态码，默认400
     */
    private final HttpStatus httpStatus;

    public BusinessException(String message) {
        super(message);
        this.code = StatusEnum.PARAM_ERROR.getCode();
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public BusinessException(Integer code, String message, HttpStatus httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public BusinessException(StatusEnum statusEnum) {
        super(statusEnum.getMsg());
        this.code = statusEnum.getCode();
        this.httpStatus = statusEnum.getHttpStatus();
    }

    public BusinessException(StatusEnum statusEnum, String message) {
        super(message);
        this.code = statusEnum.getCode();
        this.httpStatus = statusEnum.getHttpStatus();
    }

    /**
     * 将底层系统异常包装为对外业务异常，同时保留原始异常用于告警追踪
     */
    public BusinessException(StatusEnum statusEnum, String message, Throwable cause) {
        super(message, cause);
        this.code = statusEnum.getCode();
        this.httpStatus = statusEnum.getHttpStatus();
    }
}
