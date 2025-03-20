package com.ecoflow.robot.message;

/**
 * 发送消息接口
 */
public interface ISendMessage {

    /**
     * 发送消息
     *
     * @param message 消息内容
     * @return true 发送成功 false 发送失败
     * @throws Exception
     */
    boolean send(String message) throws Exception;
}
