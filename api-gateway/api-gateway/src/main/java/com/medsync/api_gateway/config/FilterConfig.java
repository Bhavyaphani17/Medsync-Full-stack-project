package com.medsync.api_gateway.config;

import com.medsync.api_gateway.filter.JwtGatewayFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<JwtGatewayFilter> jwtFilterRegistration(JwtGatewayFilter filter) {
        FilterRegistrationBean<JwtGatewayFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }
}
