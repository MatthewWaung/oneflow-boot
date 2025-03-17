package com.oneflow.core.boot.config;

import com.oneflow.core.boot.interceptor.TraceIdFilter;
import com.oneflow.core.boot.interceptor.TraceIdGenerator;
import com.oneflow.comm.utils.IdUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class TraceIdConfiguration {

    @Bean
    @ConditionalOnMissingBean(TraceIdGenerator.class)
    public TraceIdGenerator traceIdGenerator() {
        return IdUtil::generateUUIDWithoutDash;
    }

    @Bean
    public FilterRegistrationBean<TraceIdFilter> traceIdFilterRegistrationBean(TraceIdGenerator traceIdGenerator) {
        TraceIdFilter traceIdFilter = new TraceIdFilter(traceIdGenerator);
        FilterRegistrationBean<TraceIdFilter> registration = new FilterRegistrationBean<>(traceIdFilter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

}
