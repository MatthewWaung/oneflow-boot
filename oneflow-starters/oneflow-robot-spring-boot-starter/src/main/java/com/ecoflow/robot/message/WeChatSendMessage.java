package com.ecoflow.robot.message;

import com.ecoflow.robot.props.RobotProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;

import static org.springframework.util.StringUtils.hasLength;

@Slf4j
@AllArgsConstructor
public class WeChatSendMessage extends AbstractRobotSendMessage {

    private RobotProperties robotProperties;
    private RestTemplate restTemplate;

    @Override
    public boolean send(String message) throws Exception {
        // 这里调用抽象类中的request方法
        return this.request(restTemplate, new HashMap<String, Object>(2) {{
            put("msgtype", "text");
            put("text", new HashMap<String, Object>(2) {{
                put("mentioned_list", Collections.singleton("@all"));
                // 企业微信文本内容，最长不超过2048个字节，必须是utf8编码
                put("content", substringByBytes(message, 2048));
            }});
        }});
    }

    @Override
    public String getUrl() throws Exception {
        StringBuffer url = new StringBuffer();
        url.append("https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=");
        url.append(robotProperties.getWeChat().getKey());
        return url.toString();
    }

    /**
     * 按字节截取字符串
     *
     * @param str   待截取字符串
     * @param bytes 字节长度
     * @return
     */
    public static String substringByBytes(String str, int bytes) {
        if (hasLength(str)) {
            int len = 0;
            int strLength = str.length();
            for (int i = 0; i < strLength; i++) {
                // GBK 编码格式 中文占两个字节 UTF-8 编码格式中文占三个字节;
                len += (str.charAt(i) > 255 ? 3 : 1);
                if (len > bytes) {
                    return str.substring(0, i);
                }
            }
        }
        return str;
    }


}
