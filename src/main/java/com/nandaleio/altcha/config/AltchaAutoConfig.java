package com.nandaleio.altcha.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import com.nandaleio.altcha.controller.AltchaController;
import com.nandaleio.altcha.properties.AltchaSpringBootProperties;
import com.nandaleio.altcha.services.AltchaService;


@Configuration
@EnableConfigurationProperties(AltchaSpringBootProperties.class)
public class AltchaAutoConfig {

    @Bean
    @ConditionalOnMissingBean // Allows users to override if needed
    public AltchaService altchaService(AltchaSpringBootProperties properties) {
        return new AltchaService(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public AltchaController altchaController(AltchaService service) {
        return new AltchaController(service);
    }

    @Bean
    @ConditionalOnMissingBean
    public AltchaInterceptor myInterceptor(AltchaService service) {
        return new AltchaInterceptor(service);
    }

    @Bean
    public WebMvcConfigurer myInterceptorConfigurer(AltchaInterceptor myInterceptor) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(myInterceptor);
            }
        };
    }
}