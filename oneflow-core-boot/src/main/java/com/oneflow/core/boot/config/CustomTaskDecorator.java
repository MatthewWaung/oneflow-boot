package com.oneflow.core.boot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

public class CustomTaskDecorator implements TaskDecorator {

    private static final Logger logger = LoggerFactory.getLogger(CustomTaskDecorator.class);

    private static final String MDC_TOKEN = "Authorization";

    private static final String MDC_TRACE_ID = "traceId";

    @Override
    public Runnable decorate(Runnable runnable) {
        // 获取主线程的请求信息
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = null;
        if (attributes instanceof ServletRequestAttributes) {
            servletRequestAttributes = (ServletRequestAttributes) attributes;
        }

        Map<String, String> contextMap = null;
        try {
            if (servletRequestAttributes != null) {
                // 设置token
                String token = servletRequestAttributes.getRequest().getHeader(MDC_TOKEN);
                if (StringUtils.hasText(token)) {
                    MDC.put(MDC_TOKEN, token);
                }

                // 设置traceId
                String traceId = servletRequestAttributes.getRequest().getHeader(MDC_TRACE_ID);
                if (StringUtils.hasLength(traceId)) {
                    MDC.put(MDC_TRACE_ID, traceId);
                }
            }
            contextMap = MDC.getCopyOfContextMap();
        } catch (Exception e) {
            logger.error("Failed to set MDC context", e);
        }

        Map<String, String> finalContextMap = contextMap;
        return () -> {
            try {
                if (finalContextMap != null) {
                    MDC.setContextMap(finalContextMap);
                }
                if (attributes != null) {
                    // 将主线程的请求信息，设置到子线程中
                    RequestContextHolder.setRequestAttributes(attributes);
                }
                // 执行子线程
                runnable.run();
            } finally {
                // 清除MDC中的数据，防止内存泄漏
                MDC.clear();
                // 清除主线程的请求信息，防止内存泄漏
                RequestContextHolder.resetRequestAttributes();
            }
        };
    }
}
