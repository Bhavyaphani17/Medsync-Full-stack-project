package com.medsync.api_gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medsync.common.dto.ApiErrorResponse;
import com.medsync.common.security.JwtUtil;
import com.medsync.common.security.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtGatewayFilter extends OncePerRequestFilter {

    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth/login",
            "/auth/register",
            "/auth/refresh",
            "/swagger-ui",
            "/v3/api-docs",
            "/actuator"
    );

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);
        if (header == null || !header.startsWith(SecurityConstants.BEARER_PREFIX)) {
            writeUnauthorized(response, path, "Missing or invalid Authorization header");
            return;
        }

        String token = header.substring(SecurityConstants.BEARER_PREFIX.length());
        if (!jwtUtil.validateToken(token)) {
            writeUnauthorized(response, path, "Invalid or expired token");
            return;
        }

        MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(request);
        mutableRequest.putHeader(SecurityConstants.USER_EMAIL_HEADER, jwtUtil.extractEmail(token));
        mutableRequest.putHeader(SecurityConstants.USER_ROLE_HEADER, jwtUtil.extractRole(token).name());
        mutableRequest.putHeader(SecurityConstants.USER_ID_HEADER, String.valueOf(jwtUtil.extractUserId(token)));

        filterChain.doFilter(mutableRequest, response);
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private void writeUnauthorized(HttpServletResponse response, String path, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(message)
                .path(path)
                .build();

        objectMapper.writeValue(response.getWriter(), body);
    }
}
