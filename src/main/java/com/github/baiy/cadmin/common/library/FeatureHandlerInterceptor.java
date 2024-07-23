package com.github.baiy.cadmin.common.library;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.github.baiy.cadmin.common.annotation.Admin;
import com.github.baiy.cadmin.common.configure.Constants;
import com.github.baiy.cadmin.common.domain.AdminResponse;
import com.github.baiy.cadmin.common.domain.Context;
import com.github.baiy.cadmin.common.exception.AdminException;
import com.github.baiy.cadmin.common.helper.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@NoArgsConstructor
public class FeatureHandlerInterceptor implements HandlerInterceptor {
    @Getter
    private String apikey = "";

    public FeatureHandlerInterceptor(String apikey) {
        this.apikey = apikey;
    }

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler
    ) throws Exception {
        try {
            if (!(handler instanceof HandlerMethod)) {
                throw new AdminException("不支持的请求类型", 10001);
            }
            if (!((HandlerMethod) handler).hasMethodAnnotation(Admin.class)) {
                throw new AdminException("不支持的请求类型", 10002);
            }
            var context = Context.decode(request, getApikey());
            if (StrUtil.isBlank(context.getTrackId())) {
                throw new RuntimeException("trackId为空");
            }
            var featureTrackId = DigestUtil.sha256Hex(
                    IdUtil.simpleUUID() + System.currentTimeMillis()
            ).substring(0, 16);

            MDC.put(Constants.LOG_TRACK_NAME, context.getTrackId() + "," + featureTrackId);
            // 检查上下文信息
            request.setAttribute("context", context);
        } catch (Exception e) {
            log.error("解析请求上下文失败:", e);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
            response.getWriter().write(JsonUtil.toJSONString(AdminResponse.error(e)));
            return false;
        }
        return true;
    }
}
