package com.oneflow.core.boot.interceptor;

/**
 * 函数式接口，只能有一个抽象方法
 */
@FunctionalInterface
public interface TraceIdGenerator {

    /**
     * 生成traceId
     * @return traceId
     */
    String generate();

}
