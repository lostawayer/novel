package com.commons.mvc;

import com.commons.mvc.GlobalExceptionHander;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {//允许前端应用跨域访问后端资源
        CorsRegistration corsRegistration=registry.addMapping("/**");
        corsRegistration.allowPrivateNetwork(true);//允许来自私有网络
        corsRegistration.exposedHeaders(CorsConfiguration.ALL);//用于指定后端响应中允许前端访问的自定义响应头。
        //CorsConfiguration.ALL表示允许前端获取所有响应头,相当于*
        corsRegistration.allowCredentials(true);//允许跨域请求携带认证信息
        corsRegistration.allowedMethods(CorsConfiguration.ALL);//指定允许的 HTTP 请求方法
        corsRegistration.allowedHeaders(CorsConfiguration.ALL);//指定前端请求中允许携带的请求头
        corsRegistration.allowedOriginPatterns(CorsConfiguration.ALL);//指定允许跨域访问的来源
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射 /upload/** 到静态资源目录
        String projectDir = System.getProperty("user.dir");
        String uploadPath = "file:" + projectDir + "/novel-service/src/main/resources/static/upload/";
        registry.addResourceHandler("/upload/**")
                .addResourceLocations(uploadPath, "classpath:/static/upload/");
    }

    @Bean
    public GlobalExceptionHander globalExceptionHander(){
        return new GlobalExceptionHander();
    }
}
