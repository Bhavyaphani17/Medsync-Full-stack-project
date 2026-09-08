package com.medsync.common.config;

import com.medsync.common.security.GatewayHeaderAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class CommonSecurityConfig {

    private final GatewayHeaderAuthenticationFilter gatewayHeaderAuthenticationFilter;

    @Bean
    public SecurityFilterChain commonSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/actuator/**").permitAll()
                )
                .addFilterBefore(gatewayHeaderAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
