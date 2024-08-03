package com.ychat.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "ychat.jwt")
public class JwtProperties {
    private Resource location;
    private String password;
    private String alias;
    //todo 我现在设置了token可用时间为100年
    private Duration tokenTTL = Duration.ofMinutes(10);
}
