package com.ecoflow.robot.message;

import com.ecoflow.robot.props.RobotProperties;
import lombok.AllArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;

@AllArgsConstructor
public class FeishuSendException extends AbstractRobotSendMessage {

    private RobotProperties robotProperties;
    private RestTemplate restTemplate;

    @Override
    public String getUrl() throws Exception {
        StringBuilder url = new StringBuilder();
        url.append("https://open.feishu.cn/open-apis/bot/v2/hook/");
        url.append(robotProperties.getFeiShu().getKey());
        return url.toString();
    }

    @Override
    public boolean send(String message) throws Exception {
        return this.request(restTemplate, new HashMap<String, Object>(4) {{
            RobotProperties.FeiShu feiShu = robotProperties.getFeiShu();
            final String secret = feiShu.getSecret();
            if (StringUtils.hasLength(secret)) {
                final long currentTimeMillis = System.currentTimeMillis() / 1000L;
                String stringToSign = currentTimeMillis + "\n" + secret;
                // 使用HmacSHA256算法计算签名
                Mac mac = Mac.getInstance("HmacSHA256");
                mac.init(new SecretKeySpec(stringToSign.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
                byte[] signData = mac.doFinal(new byte[]{});
                put("timestamp", currentTimeMillis);
                put("sign", new String(Base64.getEncoder().encode(signData)));
            }
            put("msg_type", "text");
            put("content", new HashMap<String, Object>(1) {{
                put("text", message);
            }});
        }});
    }

}
