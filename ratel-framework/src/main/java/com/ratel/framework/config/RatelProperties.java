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

    private String userId;

    private String username;

    private String deptId;

    private String deptName;

    private String roles;

    private String permission;

}
