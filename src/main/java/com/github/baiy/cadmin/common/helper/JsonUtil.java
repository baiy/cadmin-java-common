package com.github.baiy.cadmin.common.helper;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.baiy.cadmin.common.exception.BusinessException;
import lombok.Getter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Map;

public class JsonUtil {
    @Getter
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleModule module = new SimpleModule();
        module.addSerializer(new TemporalSerializer());
        objectMapper.registerModule(module);
    }

    private JsonUtil() {
    }


    public static <T> T parseObject(String text, Class<T> clazz) {
        try {
            return objectMapper.readValue(text, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static <T> T parseObjectNullable(String text, Class<T> clazz) {
        try {
            if (StrUtil.isBlank(text)) {
                return clazz.getDeclaredConstructor().newInstance();
            }
            return objectMapper.readValue(text, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static <T> List<T> parseArray(String text) {
        try {
            return objectMapper.readValue(text, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    public static <T> Map<String, T> parseMap(String text) {
        try {
            return objectMapper.readValue(text, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    public static String toJSONString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    public static String toJSONPrettyString(Object object) {
        return JSONUtil.toJsonPrettyStr(object);
    }

    public static class TemporalSerializer extends JsonSerializer<Temporal> {

        @Override
        public void serialize(
                Temporal temporal, JsonGenerator jsonGenerator,
                SerializerProvider serializers
        ) throws IOException {
            if (temporal == null) {
                jsonGenerator.writeNull();
                return;
            }
            if (temporal instanceof LocalDate value) {
                jsonGenerator.writeString(value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                return;
            }
            if (temporal instanceof LocalTime value) {
                jsonGenerator.writeString(value.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                return;
            }
            if (temporal instanceof LocalDateTime value) {
                jsonGenerator.writeString(value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                return;
            }
            jsonGenerator.writeString(temporal.toString());
        }

        @Override
        public Class<Temporal> handledType() {
            return Temporal.class;
        }
    }
}
