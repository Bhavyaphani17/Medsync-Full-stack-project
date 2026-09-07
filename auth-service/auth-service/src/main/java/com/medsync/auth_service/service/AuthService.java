package com.medsync.auth_service.service;

import com.medsync.auth_service.dto.*;
import com.medsync.auth_service.entity.RefreshToken;
import com.medsync.auth_service.entity.User;
import com.medsync.auth_service.repository.RefreshTokenRepository;
import com.medsync.auth_service.repository.UserRepository;
import com.medsync.common.exception.BadRequestException;
import com.medsync.common.exception.ResourceNotFoundException;
import com.medsync.common.exception.UnauthorizedException;
import com.medsync.common.security.JwtProperties;
import com.medsync.common.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        User saved = userRepository.save(user);
        return toUserResponse(saved);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        refreshTokenRepository.deleteByUser_Id(user.getId());
        return buildLoginResponse(user);
    }

    @Transactional
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (storedToken.isRevoked() || storedToken.getExpiryDate().isBefore(Instant.now())) {
            throw new UnauthorizedException("Refresh token expired or revoked");
        }

        User user = storedToken.getUser();
        refreshTokenRepository.delete(storedToken);
        return buildLoginResponse(user);
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserResponse)
                .toList();
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }
        refreshTokenRepository.deleteByUser_Id(id);
        userRepository.deleteById(id);
    }

    @Transactional
    public UserResponse updateUser(Long id, RegisterRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userRepository.findByEmail(request.getEmail())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BadRequestException("Email already in use");
                });

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return toUserResponse(userRepository.save(user));
    }

    private LoginResponse buildLoginResponse(User user) {
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), user.getRole());
        String refreshTokenValue = createRefreshToken(user);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpirationMs())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .build();
    }

    private String createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(Instant.now().plusMillis(jwtProperties.getRefreshExpiration()))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken).getToken();
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
