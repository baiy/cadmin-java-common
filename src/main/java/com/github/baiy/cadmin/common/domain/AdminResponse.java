package com.github.baiy.cadmin.common.domain;

import com.github.baiy.cadmin.common.exception.AdminException;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AdminResponse {
    private Object data;
    private Boolean status;
    private Integer code;
    private String message;
    private LocalDateTime time;
    private String trackId;

    private AdminResponse(Object data, Boolean status, Integer code, String message) {
        this.data = data;
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public static AdminResponse success() {
        return success("");
    }

    public static AdminResponse success(Object data) {
        return success(data, "操作成功");
    }

    public static AdminResponse success(Object data, String message) {
        return success(data, message, 0);
    }

    public static AdminResponse success(Object data, Integer code) {
        return success(data, "操作成功", code);
    }

    public static AdminResponse success(Object data, String message, Integer code) {
        return new AdminResponse(data == null ? "" : data, true, code, message);
    }

    public static AdminResponse error() {
        return error("", "操作失败", 0);
    }

    public static AdminResponse error(String message) {
        return error("", message, 0);
    }

    public static AdminResponse error(String message, Integer code) {
        return error("", message, code);
    }

    public static AdminResponse error(Integer code) {
        return error("", "操作失败", code);
    }

    public static AdminResponse error(Throwable exception) {
        AdminResponse instance;
        if (exception instanceof AdminException e) {
            instance = error(e.getMessage(), e.getCode());
        } else {
            instance = error("系统错误");
        }
        return instance;
    }

    public static AdminResponse error(Object data, String message, Integer code) {
        return new AdminResponse(data == null ? "" : data, false, code, message);
    }
}
