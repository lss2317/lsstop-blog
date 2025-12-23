package com.lsstop.common;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.lsstop.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应类
 *
 * @author lishusheng
 * @date 2025/12/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 提示信息，如果有错误时，前端可以获取该字段进行提示
     */
    private String msg;

    /**
     * 查询到的结果数据，
     */
    private T data;

    /**
     * 成功响应，不需要返回数据
     */
    public static <T> Result<T> success() {
        return new Result(StatusEnum.SUCCESS.getCode(), StatusEnum.SUCCESS.getMsg(), null);
    }

    /**
     * 成功响应，需要返回数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(StatusEnum.SUCCESS.getCode(), StatusEnum.SUCCESS.getMsg(), data);
    }

    /**
     * 成功响应，需要返回数据跟信息
     */
    public static <T> Result<T> success(T data, String msg) {
        return new Result<>(StatusEnum.SUCCESS.getCode(), msg, data);
    }


    /**
     * 失败响应，不需要返回数据
     */
    public static <T> Result<T> failure(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    /**
     * 失败响应，不需要返回数据
     */
    public static <T> Result<T> failure() {
        return new Result<>(StatusEnum.FAILURE.getCode(), StatusEnum.FAILURE.getMsg(), null);
    }

    public static <T> Result<T> failure(String msg) {
        return new Result<>(StatusEnum.FAILURE.getCode(), msg, null);
    }

    public static <T> Result<T> failure(T data) {
        return new Result<>(StatusEnum.FAILURE.getCode(), StatusEnum.FAILURE.getMsg(), data);
    }


    /**
     * 转json字符串
     *
     * @return {@link String}
     */
    public String asJsonString() {
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }

}
