package com.ecoflow.robot.handler;

import org.aspectj.lang.JoinPoint;

/**
 * 异常消息处理器
 */
public interface IErrorMessageHandler {

    /**
     * 异常消息内容
     *
     * @param joinPoint
     * @param e
     * @return
     */
    String message(JoinPoint joinPoint, Exception e);

    /**
     * 异常消息内容
     *
     * @param e
     * @return
     */
    String message(Exception e);

}
