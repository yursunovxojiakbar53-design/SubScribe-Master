package com.example.d.auth.service;

import com.example.d.auth.dto.AuthResponse;
import com.example.d.auth.dto.RefreshTokenRequest;
import com.example.d.auth.entity.RefreshToken;
import com.example.d.auth.repository.RefreshTokenRepository;
import com.example.d.exception.InvalidTokenException;
import com.example.d.security.CustomUserDetails;
import com.example.d.user.entity.Users;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService{

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    public RefreshToken createRefreshToken(Users user) {

        RefreshToken refreshToken = RefreshToken.builder()
                        .token(UUID.randomUUID().toString())
                        .expiryDate(LocalDateTime.now().plusSeconds(refreshExpiration))
                        .revoked(false)
                        .user(user)
                        .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow(() -> new InvalidTokenException("Refresh token topilmadi"));

        if (refreshToken.isRevoked()) {
            throw new InvalidTokenException("Refresh token revoke qilingan");
        }

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Refresh token muddati tugagan");
        }

        return refreshToken;
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {

        RefreshToken refreshToken = verifyToken(request.refreshToken());
        UserDetails userDetails = new CustomUserDetails(refreshToken.getUser());
        String accessToken = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .build();
    }

    public void revokeToken(String token) {

        RefreshToken refreshToken = verifyToken(token);

        refreshToken.setRevoked(true);

        refreshTokenRepository.save(refreshToken);
    }

}