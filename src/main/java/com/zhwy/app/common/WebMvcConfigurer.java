package com.zhwy.app.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfigurer extends WebMvcConfigurerAdapter {

    @Value("${filePath}")
    String filePath;
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/file/**").addResourceLocations("file:"+filePath);
       // registry.addResourceHandler("/img/**").addResourceLocations("file:F:/Liaoning/imgs/grads/");
        super.addResourceHandlers(registry);
    }


}
