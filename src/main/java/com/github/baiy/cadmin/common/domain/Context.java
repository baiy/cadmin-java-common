package com.github.baiy.cadmin.common.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.github.baiy.cadmin.common.helper.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

// 基础信息
@Getter
@Setter
public class Context {
    public static final String HTTP_HEADER_NAME = "Cadmin-Context";

    private String trackId = "";
    private Long userId = 0L;
    private String username = "";
    private String action = "";
    private String ip = "";
    private Boolean administrator = false;
    // 为防止信息泄露 这个token不是完整token
    private String token = "";

    public String encode(String apikey) {
        if (StrUtil.isBlank(apikey)) {
            throw new RuntimeException("apikey 不能为空");
        }
        var context = new Context();
        BeanUtil.copyProperties(this, context);
        var transmit = new Transmit();
        transmit.setTime(System.currentTimeMillis());
        transmit.setContext(JsonUtil.toJSONString(context));
        transmit.setSign(transmit.calculateSign(apikey));
        return Base64.encode(JsonUtil.toJSONString(transmit));
    }

    public static Context decode(HttpServletRequest request, String apikey) {
        if (StrUtil.isBlank(apikey)) {
            throw new RuntimeException("apikey 不能为空");
        }
        var content = request.getHeader(HTTP_HEADER_NAME);
        if (StrUtil.isBlank(content)) {
            throw new RuntimeException("上下文信息不能为空");
        }
        var transmit = JsonUtil.parseObject(Base64.decodeStr(content), Transmit.class);
        if ((System.currentTimeMillis() - transmit.getTime()) > 20 * 1000) {
            throw new RuntimeException("请求已过期");
        }
        if (!transmit.getSign().equals(transmit.calculateSign(apikey))) {
            throw new RuntimeException("验证失败");
        }
        return JsonUtil.parseObject(transmit.getContext(), Context.class);
    }

    @Data
    public static class Transmit {
        private Long time;
        private String context;
        private String sign;

        public String calculateSign(String apikey) {
            return DigestUtil.sha256Hex(context + apikey + time);
        }
    }
}
