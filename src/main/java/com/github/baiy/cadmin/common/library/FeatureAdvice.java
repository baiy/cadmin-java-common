package com.github.baiy.cadmin.common.library;

import com.github.baiy.cadmin.common.domain.AdminResponse;
import com.github.baiy.cadmin.common.helper.JsonUtil;
import jakarta.servlet.ServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import com.github.baiy.cadmin.common.annotation.Admin;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@Slf4j
public class FeatureAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(
            MethodParameter returnType,
            @NonNull Class<? extends HttpMessageConverter<?>> converterType
    ) {
        return returnType.hasMethodAnnotation(Admin.class);
    }


    @Override
    public Object beforeBodyWrite(
            Object body,
            @NonNull MethodParameter returnType,
            @NonNull MediaType selectedContentType,
            @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response
    ) {
        if (body instanceof ServletResponse) {
            return null;
        }

        // json 响应
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        var result = responseHandle(body);
        if (result != null) {
            return AdminResponse.success(result);
        }

        if (body instanceof String) {
            return JsonUtil.toJSONString(AdminResponse.success(body));
        }
        if (body instanceof AdminResponse) {
            return body;
        }

        return AdminResponse.success(body);
    }

    public Object responseHandle(Object body) {
        return null;
    }

    public static class WithExceptionHandler extends FeatureAdvice {
        @ExceptionHandler(Exception.class)
        @ResponseStatus(HttpStatus.OK)
        public AdminResponse handleException(
                Exception e,
                HandlerMethod handlerMethod
        ) throws Exception {
            if (handlerMethod == null || !handlerMethod.hasMethodAnnotation(Admin.class)) {
                throw e;
            }
            log.error("cadmin feature exception:", e);
            if (e instanceof MethodArgumentNotValidException) {
                return AdminResponse.error("请求参数不合法");
            }
            return AdminResponse.error(e);
        }
    }
}
