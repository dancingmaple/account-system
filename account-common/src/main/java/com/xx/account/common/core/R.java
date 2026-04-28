package com.xx.account.common.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一响应封装类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer code;
    private String message;
    private T data;
    private Long timestamp;

    public static <T> R<T> ok() {
        return ok(null);
    }

    public static <T> R<T> ok(T data) {
        return R.<T>builder()
                .code(ResultCode.SUCCESS.getCode())
                .message(ResultCode.SUCCESS.getMessage())
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static <T> R<T> error(String message) {
        return R.<T>builder()
                .code(ResultCode.ERROR.getCode())
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static <T> R<T> error(Integer code, String message) {
        return R.<T>builder()
                .code(code)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static <T> R<T> error(ResultCode resultCode) {
        return R.<T>builder()
                .code(resultCode.getCode())
                .message(resultCode.getMessage())
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public boolean isSuccess() {
        return ResultCode.SUCCESS.getCode().equals(this.code);
    }
}
