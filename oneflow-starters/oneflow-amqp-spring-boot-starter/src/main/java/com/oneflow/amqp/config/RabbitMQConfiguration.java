package com.oneflow.amqp.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.oneflow.amqp.props.AmqpProperties;
import com.oneflow.amqp.publisher.AmqpPublisher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@AllArgsConstructor
@EnableConfigurationProperties(AmqpProperties.class)
public class RabbitMQConfiguration {

    private final AmqpProperties amqpProperties;
    private final ConnectionFactory connectionFactory;

    /**
     * 配置 RabbitTemplate, 设置消息确认回调和返回回调
     */
    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        rabbitTemplate.setMandatory(true);

        // 消息发送到交换机的确认回调
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("消息成功发送到Exchange，correlationData={}", correlationData);
            } else {
                log.warn("消息发送到Exchange失败，correlationData={}, cause={}", correlationData, cause);
            }
        });

        // 消息从交换机转发到队列失败的回调
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            log.warn("消息Exchange转发到Queue失败，message={}, replyCode={}, replyText={}, exchange={}, routingKey={}",
                    message, replyCode, replyText, exchange, routingKey);
        });

        return rabbitTemplate;
    }

    @Bean
    @ConditionalOnMissingBean
    public AmqpPublisher amqpPublisher(RabbitTemplate rabbitTemplate) {
        return new AmqpPublisher(rabbitTemplate);
    }

    /**
     * 配置并发消费者的容器工厂，使用动态配置的消费者数量和预取数量
     */
    @Bean("parallelContainerFactory")
    @ConditionalOnMissingBean
    public SimpleRabbitListenerContainerFactory containerFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setPrefetchCount(amqpProperties.getPrefetchCount());  // 动态获取预取数量
        factory.setConcurrentConsumers(amqpProperties.getConcurrentConsumers());  // 动态获取并发消费者数
        configurer.configure(factory, connectionFactory);
        return factory;
    }

    /**
     * 配置消息转换器，使用 JSON 序列化
     */
    @Bean
    @ConditionalOnMissingBean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
//        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 禁用时间戳方式
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return new Jackson2JsonMessageConverter(mapper);
    }

}
