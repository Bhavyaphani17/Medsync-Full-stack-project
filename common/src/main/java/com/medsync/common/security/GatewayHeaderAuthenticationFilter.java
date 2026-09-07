package com.medsync.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GatewayHeaderAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String email = request.getHeader(SecurityConstants.USER_EMAIL_HEADER);
        String role = request.getHeader(SecurityConstants.USER_ROLE_HEADER);

        if (email != null && role != null) {
            setAuthentication(email, role);
        } else {
            String header = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);
            if (header != null && header.startsWith(SecurityConstants.BEARER_PREFIX)) {
                String token = header.substring(SecurityConstants.BEARER_PREFIX.length());
                if (jwtUtil.validateToken(token)) {
                    setAuthentication(jwtUtil.extractEmail(token), jwtUtil.extractRole(token).name());
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String email, String role) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                email,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
