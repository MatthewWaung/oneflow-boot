package com.oneflow.amqp.publisher;

import com.alibaba.fastjson.JSONObject;
import com.oneflow.amqp.message.MsgBody;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
public class AmqpPublisher {

    private RabbitTemplate rabbitTemplate;

    /**
     * 获取消息对象
     *
     * @param msgBody
     * @param messageId
     * @return
     */
    private Message getMessage(MsgBody msgBody, String messageId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        String msg = JSONObject.toJSONString(msgBody);
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setMessageId(messageId);
        messageProperties.setContentType("application/json");
        messageProperties.setContentEncoding("UTF-8");
        messageProperties.setHeader("createTime", formatter.format(LocalDateTime.now()));
        return new Message(msg.getBytes(), messageProperties);
    }

    /**
     * 发布消息到消息队列
     *
     * @param exchange
     * @param routingKey
     * @param msgBody
     */
    public void publish(String exchange, String routingKey, MsgBody msgBody) {
        String messageId = UUID.randomUUID().toString();
        rabbitTemplate.convertAndSend(exchange, routingKey, getMessage(msgBody, messageId), new CorrelationData(messageId));
        log.info("========Message delivered successfully, message id: {}=======", messageId);
    }


    /**
     * 发布消息到消息队列
     *
     * @param exchange
     * @param routingKey
     * @param object
     */
    public void publish(String exchange, String routingKey, Object object) {
        // 生成唯一的 messageId
        String messageId = UUID.randomUUID().toString();

        // 通过 MessagePostProcessor 设置消息属性中的 messageId
        rabbitTemplate.convertAndSend(exchange, routingKey, object, message -> {
            message.getMessageProperties().setMessageId(messageId); // 设置 messageId
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
            message.getMessageProperties().setMessageId(messageId);
            message.getMessageProperties().setContentType("application/json");
            message.getMessageProperties().setContentEncoding("UTF-8");
            message.getMessageProperties().setHeader("createTime", formatter.format(LocalDateTime.now()));
            message.getMessageProperties().setHeader("x-retry-count", 0);
            return message;
        }, new CorrelationData(messageId));  // 继续使用 CorrelationData 处理消息确认
        log.info("AmqpPublisher publish msg delivered successfully messageId: {} ", messageId);
    }

}
