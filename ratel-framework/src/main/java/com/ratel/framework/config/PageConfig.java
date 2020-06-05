package com.ratel.framework.config;

import com.ratel.framework.web.method.support.MultiRequestBodyArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Pageable最大size的自定义配置
 */
//@Configuration
public class PageConfig extends WebMvcConfigurerAdapter {

    private static final int PMP_MAX_PAGE_SIZE = 10000;

    @Bean
    public HttpMessageConverter<String> responseBodyConverter() {
        return new StringHttpMessageConverter(Charset.forName("UTF-8"));
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.configureMessageConverters(converters);
        converters.add(responseBodyConverter());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();

        resolver.setMaxPageSize(PMP_MAX_PAGE_SIZE);
        argumentResolvers.add(resolver);
        argumentResolvers.add(new MultiRequestBodyArgumentResolver());
        super.addArgumentResolvers(argumentResolvers);
    }
}

