package com.github.baiy.cadmin.common.exception;

import lombok.Getter;

@Getter
public class AdminException extends RuntimeException {
    private final Integer code;

    public AdminException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public AdminException(String message) {
        super(message);
        this.code = 0;
    }
}
