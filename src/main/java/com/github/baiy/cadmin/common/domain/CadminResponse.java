package com.github.baiy.cadmin.common.domain;

import com.github.baiy.cadmin.common.exception.BusinessException;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CadminResponse {
    private Object data;
    private Boolean status;
    private Integer code;
    private String message;
    private LocalDateTime time;
    private String trackId;

    private CadminResponse(Object data, Boolean status, Integer code, String message) {
        this.data = data;
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public static CadminResponse success() {
        return success("");
    }

    public static CadminResponse success(Object data) {
        return success(data, "操作成功");
    }

    public static CadminResponse success(Object data, String message) {
        return success(data, message, 0);
    }

    public static CadminResponse success(Object data, Integer code) {
        return success(data, "操作成功", code);
    }

    public static CadminResponse success(Object data, String message, Integer code) {
        return new CadminResponse(data == null ? "" : data, true, code, message);
    }

    public static CadminResponse error() {
        return error("", "操作失败", 0);
    }

    public static CadminResponse error(String message) {
        return error("", message, 0);
    }

    public static CadminResponse error(String message, Integer code) {
        return error("", message, code);
    }

    public static CadminResponse error(Integer code) {
        return error("", "操作失败", code);
    }

    public static CadminResponse error(Throwable exception) {
        CadminResponse instance;
        if (exception instanceof BusinessException e) {
            instance = error(e.getMessage(), e.getCode());
        } else {
            instance = error("系统错误");
        }
        return instance;
    }

    public static CadminResponse error(Object data, String message, Integer code) {
        return new CadminResponse(data == null ? "" : data, false, code, message);
    }
}
