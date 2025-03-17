package com.oneflow.comm.jackson;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "oneflow.jackson")
public class JacksonProperties {

    /**
     * 是否启用
     */
    private Boolean enabled = Boolean.TRUE;

    /**
     * 支持 MediaType text/plain，用于和 blade-api-crypto 一起使用
     */
    private Boolean supportTextPlain = Boolean.FALSE;

}
