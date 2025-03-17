package com.oneflow.core.boot.interceptor;

import com.oneflow.core.boot.utils.ContextHolder;
import lombok.AllArgsConstructor;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@AllArgsConstructor
public class TraceIdFilter extends OncePerRequestFilter {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String TRACE_ID_KEY = "traceId";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private TraceIdGenerator traceIdGenerator;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String traceId = getCurrentTraceId(request, traceIdGenerator);
        String authorization = request.getHeader(AUTHORIZATION_HEADER);

        MDC.put(TRACE_ID_KEY, traceId);
        // 将traceId添加到ContextHolder中，方便后续使用
        ContextHolder.set(ContextHolder.TRACE_ID, traceId);
        ContextHolder.set(ContextHolder.AUTHORIZATION, authorization);

        try {
            // 响应头添加traceId，方便排查问题
            response.addHeader(TRACE_ID_HEADER, traceId);
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(TRACE_ID_KEY);
            ContextHolder.remove();
        }
    }

    private String getCurrentTraceId(HttpServletRequest request, TraceIdGenerator traceIdGenerator) {
        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (traceId == null || traceId.isEmpty()) {
            traceId = traceIdGenerator.generate();
        }
        return traceId;
    }

}
