package com.ecoflow.robot.message;

import com.ecoflow.robot.props.RobotProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;

@Slf4j
@AllArgsConstructor
public class DingTalkSendMessage extends AbstractRobotSendMessage {

    private RobotProperties robotProperties;
    private RestTemplate restTemplate;

    @Override
    public String getUrl() throws Exception {
        RobotProperties.DingTalk dingTalk = robotProperties.getDingTalk();
        StringBuffer url = new StringBuffer();
        url.append("https://oapi.dingtalk.com/robot/send?access_token=");
        url.append(dingTalk.getAccessToken());
        String secret = dingTalk.getSecret();
        if (StringUtils.hasLength(secret)) {
            Long timestamp = System.currentTimeMillis();
            url.append("&timestamp=").append(timestamp);
            String sign = encodeBase64HmacSHA256(secret, timestamp + "\n" + secret);
            url.append("&sign=").append(URLEncoder.encode(sign, "UTF-8"));
        }
        return url.toString();
    }

    @Override
    public boolean send(String message) throws Exception {
        return this.request(restTemplate, new HashMap<String, Object>(3) {{
            put("msgtype", "text");
            put("at", new HashMap<String, Object>(1) {{
                put("isAtAll", true);
            }});
            put("text", new HashMap<String, Object>(1) {{
                put("content", message);
            }});
        }});
    }


    /**
     * Base64 HmacSHA256 算法签名
     *
     * @param secret 密钥
     * @param input  加密内容
     * @return
     * @throws Exception
     */
    public static String encodeBase64HmacSHA256(String secret, String input) throws Exception {
        return encodeBase64Hmac("HmacSHA256", secret, input);
    }

    /**
     * Base64 MAC 算法签名
     *
     * @param algorithm MAC算法支持 HmacMD5 HmacSHA1 HmacSHA256
     * @param secret    密钥
     * @param input     加密内容
     * @return
     * @throws Exception
     */
    public static String encodeBase64Hmac(String algorithm, String secret, String input) throws Exception {
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), algorithm));
        byte[] signData = mac.doFinal(input.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(signData);
    }


}
