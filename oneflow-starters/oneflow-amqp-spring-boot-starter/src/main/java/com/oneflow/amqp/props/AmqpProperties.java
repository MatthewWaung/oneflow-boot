package com.oneflow.amqp.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = AmqpProperties.PREFIX)
public class AmqpProperties {

    public static final String PREFIX = "oneflow.amqp";

    /**
     * 交换机
     */
    private Exchange exchange;

    /**
     * 路由键
     */
    private RoutingKey routingKey;

    @Getter
    @Setter
    public static class Exchange {
        private String omsOrder;
        private String sap;
    }

    @Getter
    @Setter
    public static class RoutingKey {
        private String qianyi;
        private String guanyi;
        private String ecang;
        private String lazada;
        private String earthtech;
        private String jumia;
    }

    /**
     * 消费者一次只会从 RabbitMQ 队列中拉取消息数量
     */
    private Integer prefetchCount = 10;

    /**
     * 同时运行的消费者线程数，即并发消费者的数量
     */
    private Integer concurrentConsumers = 10;

}
