package com.ecoflow.robot.exception;

import org.aspectj.lang.JoinPoint;

/**
 * 异常发送接口
 */
public interface ISendException {

    /**
     * 发送异常内容
     *
     * @param joinPoint
     * @param e
     * @return
     */
    boolean send(JoinPoint joinPoint, Exception e);

    /**
     * 发送异常内容
     *
     * @param e 异常内容
     * @return
     */
    boolean send(Exception e);

    /**
     * 发送异常内容
     *
     * @param message 消息
     * @return
     */
    boolean send(String message);

}
