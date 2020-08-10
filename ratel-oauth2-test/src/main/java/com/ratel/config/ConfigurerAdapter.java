package com.ratel.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.TimeZone;

/**
 * WebMvcConfigurer
 */
@Configuration
@EnableWebMvc
public class ConfigurerAdapter implements WebMvcConfigurer {

    @Value("${ratel.file.path}")
    private String path;

    @Value("${ratel.file.avatar}")
    private String avatar;

    @Value("${ratel.file.html}")
    private String html;

    @Value("${ratel.file.jarHtml}")
    private String jarHtml;

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String avatarUtl = "file:" + avatar.replace("\\", "/");
        String pathUtl = "file:" + path.replace("\\", "/");
        String htmlUtl = "file:" + html.replace("\\", "/");
        String jarHtmlUtl = jarHtml.replace("\\", "/");
        registry.addResourceHandler("/avatar/**").addResourceLocations(avatarUtl).setCachePeriod(0);
        registry.addResourceHandler("/file/**").addResourceLocations(pathUtl).setCachePeriod(0);
        if (!"none".equals(jarHtml)) {
            registry.addResourceHandler("/**").addResourceLocations(jarHtmlUtl).setCachePeriod(0);
        } else {
            registry.addResourceHandler("/**").addResourceLocations(htmlUtl).setCachePeriod(0);
        }
        registry.addResourceHandler("/statics/**").addResourceLocations("classpath:/statics/");
        // 解决 SWAGGER 404报错
        registry.addResourceHandler("/swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");

    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = converter.getObjectMapper();
        // 生成JSON时,将所有Long转换成String
//        SimpleModule simpleModule = new SimpleModule();
//        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
//        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
//        objectMapper.registerModule(simpleModule);
        // 时间格式化
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//        objectMapper.setDateFormat(new SimpleDateFormat(DateUtil.PATTERN_TIME));
        // 设置格式化内容
        converter.setObjectMapper(objectMapper);
        converters.add(0, converter);
    }
}
