package com.oneflow.amqp.message;

import com.oneflow.amqp.constant.AmqpConstant;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class OmsOrderPushQianYiMessage implements Serializable {

    public static final String suffix = "PUSH_QIAN_YI";

    // 使用业务 ID 和版本号生成的队列名称
    public final static String QUEUE = AmqpConstant.QUEUE_PREFIX + suffix;
    public final static String EXCHANGE = AmqpConstant.EXCHANGE_PREFIX + suffix;
    public final static String ROUTING_KEY = AmqpConstant.ROUTING_KEY_PREFIX + suffix;

    // 死信队列、死信交换机和路由键
    public final static String DEAD_LETTER_QUEUE = AmqpConstant.DEAD_LETTER_QUEUE_PREFIX + suffix;
    public final static String DEAD_LETTER_EXCHANGE = AmqpConstant.DEAD_LETTER_EXCHANGE_PREFIX + suffix;
    public final static String DEAD_LETTER_ROUTING_KEY = AmqpConstant.DEAD_LETTER_ROUTING_KEY_PREFIX + suffix;

    /**
     * 消息编号
     */
    private String orderNo;
    /**
     * 业务主键
     */
    private String platformOrderNo;

}


