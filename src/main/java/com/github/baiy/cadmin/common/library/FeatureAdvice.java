package com.github.baiy.cadmin.common.library;

import com.github.baiy.cadmin.common.domain.CadminResponse;
import com.github.baiy.cadmin.common.helper.JsonUtil;
import jakarta.servlet.ServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import com.github.baiy.cadmin.common.annotation.Cadmin;
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
            @NonNull MethodParameter returnType,
            @NonNull Class<? extends HttpMessageConverter<?>> converterType
    ) {
        return returnType.hasMethodAnnotation(Cadmin.class);
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

        // 自定义响应处理器
        var result = responseHandle(body);
        if (result != null) {
            return CadminResponse.success(result);
        }

        if (body instanceof String) {
            return JsonUtil.toJSONString(CadminResponse.success(body));
        }
        if (body instanceof CadminResponse) {
            return body;
        }

        return CadminResponse.success(body);
    }

    // 自定义响应处理器
    public Object responseHandle(Object body) {
        return null;
    }

    public static class WithExceptionHandler extends FeatureAdvice {
        @ExceptionHandler(Exception.class)
        @ResponseStatus(HttpStatus.OK)
        public CadminResponse handleException(
                Exception e,
                HandlerMethod handlerMethod
        ) throws Exception {
            if (handlerMethod == null || !handlerMethod.hasMethodAnnotation(Cadmin.class)) {
                throw e;
            }
            log.error("cadmin feature exception:", e);
            if (e instanceof MethodArgumentNotValidException) {
                return CadminResponse.error("请求参数不合法");
            }
            return CadminResponse.error(e);
        }
    }
}
