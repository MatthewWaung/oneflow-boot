package com.oneflow.amqp.constant;

public class AmqpConstant {

    // 使用业务 ID 和版本号生成的队列名称
    public final static String QUEUE_PREFIX = "QUEUE_OMS_ORDER_";
    public final static String EXCHANGE_PREFIX = "EXCHANGE_OMS_ORDER_";
    public final static String ROUTING_KEY_PREFIX = "ORDER.OMS.ORDER.";

    // 死信队列、死信交换机和路由键
    public final static String DEAD_LETTER_QUEUE_PREFIX = "QUEUE_DEAD_LETTER_OMS_ORDER_";
    public final static String DEAD_LETTER_EXCHANGE_PREFIX = "EXCHANGE_DEAD_LETTER_OMS_ORDER_";
    public final static String DEAD_LETTER_ROUTING_KEY_PREFIX = "ORDER.OMS.ORDER.DEAD.";
}
