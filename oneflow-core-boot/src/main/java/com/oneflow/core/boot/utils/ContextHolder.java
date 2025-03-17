package com.oneflow.core.boot.utils;

import com.alibaba.ttl.TransmittableThreadLocal;
import java.util.LinkedHashMap;
import java.util.Map;

public class ContextHolder {

    public static final String TRACE_ID = "traceId";
    public static final String AUTHORIZATION = "Authorization";

    private static final ThreadLocal<Map<String, Object>> transmittableThreadLocal = new TransmittableThreadLocal() {
        @Override
        public Object copy(Object parentValue) {
            if (parentValue instanceof Map) {
                return new LinkedHashMap<String, Object>((Map) parentValue);
            }
            return super.copy(parentValue);
        }

        @Override
        protected Object childValue(Object parentValue) {
            if (parentValue instanceof Map) {
                return new LinkedHashMap<String, Object>((Map) parentValue);
            }
            return super.childValue(parentValue);
        }

        @Override
        protected Object initialValue() {
            return new LinkedHashMap<String, Object>();
        }
    };

    public static void set(String key, Object value) {
        transmittableThreadLocal.get().put(key, value);
    }

    public static String getTraceId() {
        Object object = transmittableThreadLocal.get().get(ContextHolder.TRACE_ID);
        if (object == null) {
            // 可以添加日志记录
            // logger.info("Trace ID not found in context");
            return null;
        }
        return String.valueOf(object);
    }

    public static String getAuthorization() {
        Object object = transmittableThreadLocal.get().get(ContextHolder.AUTHORIZATION);
        if (object == null) {
            // 可以添加日志记录
            // logger.info("Authorization not found in context");
            return null;
        }
        return String.valueOf(object);
    }

    public static <T> T get(String key) {
        Object object = transmittableThreadLocal.get().get(key);
        if (object == null) {
            return null;
        }
        try {
            return (T) object;
        } catch (ClassCastException e) {
            // 可以添加日志记录
            // logger.error("Class cast exception when getting value from context", e);
            return null;
        }
    }

    public static Map<String, Object> get() {
        return transmittableThreadLocal.get();
    }

    public static void remove() {
        transmittableThreadLocal.remove();
    }
}