package com.oneflow.core.boot.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "oneflow.thread-pool")
public class ThreadPoolProperties {

    /**
     * 核心线程数，默认：10
     */
    private int corePoolSize = 10;
    /**
     * 最大线程数，默认：50
     */
    private int maxPoolSize = 50;
    /**
     * 队列容量，默认：10000
     */
    private int queueCapacity = 10000;
    /**
     * 线程存活时间，默认：300
     */
    private int keepAliveSeconds = 300;

}
