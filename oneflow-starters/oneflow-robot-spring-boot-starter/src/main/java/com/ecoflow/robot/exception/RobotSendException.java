package com.ecoflow.robot.exception;

import com.ecoflow.robot.handler.IErrorMessageHandler;
import com.ecoflow.robot.message.ISendMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;

import java.util.List;

@Slf4j
@AllArgsConstructor
public class RobotSendException implements ISendException {

    /**
     * 将ISendMessage的实现类放入List中，是通过 Spring 的依赖注入机制从 Spring 上下文中找到所有 ISendMessage 类型的 Bean，并将它们放入一个 List 中。
     * 具体来说，在 RobotAutoConfiguration 配置类中，定义了多个 ISendMessage 接口的实现类 Bean，例如 DingTalkSendMessage、WeChatSendMessage 和 FeiShuSendMessage。这些 Bean 的创建条件是基于配置文件中的属性。
     */
    private List<ISendMessage> sendMessageList;

    private IErrorMessageHandler errorMessageHandler;

    @Override
    public boolean send(JoinPoint joinPoint, Exception e) {
        try {
            String message = errorMessageHandler.message(joinPoint, e);
            for (ISendMessage sendMessage : sendMessageList) {
                sendMessage.send(message);
            }
            return true;
        } catch (Exception ex) {
            log.info("ISendException has exception: ", ex);
            return false;
        }
    }

    @Override
    public boolean send(Exception e) {
        try {
            String errorMessage = errorMessageHandler.message(e);
            for (ISendMessage sendMessage : sendMessageList) {
                sendMessage.send(errorMessage);
            }
            return true;
        } catch (Exception ex) {
            log.info("ISendException has exception: ", ex);
            return false;
        }
    }

    @Override
    public boolean send(String message) {
        try {
            for (ISendMessage sendMessage : sendMessageList) {
                sendMessage.send(message);
            }
            return true;
        } catch (Exception ex) {
            log.info("ISendException has exception: ", ex);
            return false;
        }
    }

}
