package com.ecoflow.robot.config;

import com.ecoflow.robot.aspect.ExceptionAspect;
import com.ecoflow.robot.exception.ISendException;
import com.ecoflow.robot.exception.RobotSendException;
import com.ecoflow.robot.handler.DefaultErrorMessageHandler;
import com.ecoflow.robot.handler.IErrorMessageHandler;
import com.ecoflow.robot.message.DingTalkSendMessage;
import com.ecoflow.robot.message.FeishuSendException;
import com.ecoflow.robot.message.ISendMessage;
import com.ecoflow.robot.message.WeChatSendMessage;
import com.ecoflow.robot.props.RobotProperties;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
@AllArgsConstructor
@EnableConfigurationProperties({RobotProperties.class})
@ConditionalOnProperty(prefix = RobotProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = false)
public class RobotConfiguration {

    private RobotProperties robotProperties;

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @ConditionalOnMissingBean
    public IErrorMessageHandler errorMessageHandler() {
        return new DefaultErrorMessageHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public RobotSendException robotSendException(List<ISendMessage> sendMessageList, IErrorMessageHandler errorMessageHandler) {
        return new RobotSendException(sendMessageList, errorMessageHandler);
    }

    @Bean
    @ConditionalOnProperty(prefix = RobotProperties.PREFIX, name = "enableDefaultRobot", havingValue = "true", matchIfMissing = false)
    public ExceptionAspect exceptionAspect(ISendException sendException) {
        return new ExceptionAspect(sendException);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = RobotProperties.PREFIX, name = "dingTalk.accessToken")
    public DingTalkSendMessage dingTalkSendMessage(RestTemplate restTemplate) {
        return new DingTalkSendMessage(robotProperties, restTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = RobotProperties.PREFIX, name = "weChat.key")
    public WeChatSendMessage weChatSendMessage(RestTemplate restTemplate) {
        return new WeChatSendMessage(robotProperties, restTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = RobotProperties.PREFIX, name = "feiShu.key")
    public FeishuSendException feishuSendException(RestTemplate restTemplate) {
        return new FeishuSendException(robotProperties, restTemplate);
    }

}
