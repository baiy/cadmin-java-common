package com.github.baiy.cadmin.common.exception;

import lombok.Getter;

// 业务异常 相关错误信息会直接对外输出
// CadminResponse.error(Throwable exception)
@Getter
public class BusinessException extends RuntimeException {
    private final Integer code;

    public BusinessException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message) {
        super(message);
        this.code = 0;
    }
}
