package com.ecoflow.robot.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = RobotProperties.PREFIX)
public class RobotProperties {

    public static final String PREFIX = "oneflow.robot";

    /**
     * 是否开启
     */
    private String enable;

    /**
     * 企业微信
     */
    private WeChat weChat;

    /**
     * 钉钉
     */
    private DingTalk dingTalk;

    /**
     * 飞书
     */
    private FeiShu feiShu;

    @Getter
    @Setter
    public static class WeChat {
        private String key;

    }

    @Getter
    @Setter
    public static class DingTalk {
        private String accessToken;
        private String secret;

    }

    @Getter
    @Setter
    public static class FeiShu {
        private String key;
        private String secret;
    }

}
