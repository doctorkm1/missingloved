package com.alkamel.missingloved.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Missing person images
        registry.addResourceHandler("/uploads/missing/images/**")
                .addResourceLocations("file:C:/kamel/alkwebsite/missingloved/uploads/missing/images/");

        // Missing person documents (new line added)
        registry.addResourceHandler("/uploads/missing/documents/**")
                .addResourceLocations("file:C:/kamel/alkwebsite/missingloved/uploads/missing/documents/");

        // Found person images
        registry.addResourceHandler("/uploads/found/images/**")
                .addResourceLocations("file:C:/kamel/alkwebsite/missingloved/uploads/found/images/");
        registry.addResourceHandler("/uploads/found/documents/**")
                .addResourceLocations("file:C:/kamel/alkwebsite/missingloved/uploads/found/documents/");

    }
}
