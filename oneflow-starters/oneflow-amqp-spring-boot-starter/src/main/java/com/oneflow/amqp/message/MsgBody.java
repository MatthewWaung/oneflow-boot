package com.oneflow.amqp.message;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class MsgBody {

    /**
     * 目标消息存储表id
     */
    private String infoId;

    /**
     * 消息源类型
     */
    private String msgType;

    /**
     * 消息源类型描述
     */
    private String msgTypeDesc;

    /**
     * 信息
     */
    private JSONObject msgInfo;

}
