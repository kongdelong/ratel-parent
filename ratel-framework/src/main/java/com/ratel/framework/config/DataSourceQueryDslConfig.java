package com.ratel.framework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.SimpleEntityPathResolver;

/**
 * query dsl 配置
 */
//@Configuration
public class DataSourceQueryDslConfig {

    /**
     * 配置 query dsl 的前缀
     *
     * @return
     */
    @Bean
    public EntityPathResolver entityPathResolver() {
        return new ProjectEntityPathResolver(".dsl");
    }

    public static class ProjectEntityPathResolver extends SimpleEntityPathResolver implements EntityPathResolver {

        /**
         * Creates a new {@link SimpleEntityPathResolver} with the given query package suffix.
         *
         * @param querySuffix must not be {@literal null}.
         */
        public ProjectEntityPathResolver(String querySuffix) {
            super(querySuffix);
        }
    }
}

