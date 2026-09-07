package com.medsync.common.config;

import com.medsync.common.security.SecurityConstants;
import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor headerForwardingInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return;
            }
            HttpServletRequest request = attributes.getRequest();

            forwardHeader(request, requestTemplate, SecurityConstants.AUTHORIZATION_HEADER);
            forwardHeader(request, requestTemplate, SecurityConstants.USER_EMAIL_HEADER);
            forwardHeader(request, requestTemplate, SecurityConstants.USER_ROLE_HEADER);
            forwardHeader(request, requestTemplate, SecurityConstants.USER_ID_HEADER);
        };
    }

    private void forwardHeader(HttpServletRequest request, feign.RequestTemplate requestTemplate, String headerName) {
        String value = request.getHeader(headerName);
        if (value != null) {
            requestTemplate.header(headerName, value);
        }
    }
}