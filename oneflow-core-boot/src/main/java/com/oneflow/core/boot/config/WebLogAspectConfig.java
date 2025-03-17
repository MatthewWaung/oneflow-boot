package com.oneflow.core.boot.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "oneflow.web.log")
public class WebLogAspectConfig {

    @Getter
    private static boolean enabled;

    public void setEnabled(boolean enabled) {
        WebLogAspectConfig.enabled = enabled;
    }

}
