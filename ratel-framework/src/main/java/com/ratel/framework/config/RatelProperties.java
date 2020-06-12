package com.ratel.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Framework参数配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ratel.framework")
public class RatelProperties {

    private Boolean security;

    private String username;

    private String permission;

}
