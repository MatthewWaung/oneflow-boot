package com.ecoflow.robot.message;

import com.ecoflow.robot.exception.ISendException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 机器人发送消息抽象类
 */
public abstract class AbstractRobotSendMessage implements ISendMessage {

    public boolean request(RestTemplate restTemplate, Map<String, Object> objectMap) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HashMap<String, Object> response = restTemplate.postForEntity(this.getUrl(),
                new HttpEntity<>(objectMap, headers), HashMap.class).getBody();
        if(null != response && Objects.equals(response.get("errcode"), 0)) {
            return true;
        }

        // 抛出异常消息
        throw new RuntimeException((String) response.get("errmsg"));
    }

    /**
     * 获取请求地址
     *
     * @return
     * @throws Exception
     */
    public abstract String getUrl() throws Exception;

}
