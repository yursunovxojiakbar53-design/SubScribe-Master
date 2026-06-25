package com.example.d.auth.controller;

import com.example.d.auth.dto.AuthResponse;
import com.example.d.auth.dto.RefreshTokenRequest;
import com.example.d.auth.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/refresh")
@RequiredArgsConstructor
@Tag(name = "Auth - Refresh Token", description = "Access tokenni yangilash va sessiyani yakunlash")
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;

    @PostMapping
    @Operation(summary = "Refresh token orqali yangi access token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(refreshTokenService.refreshToken(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "Refresh tokenni bekor qilish")
    public ResponseEntity<Void> logout(@RequestBody RefreshTokenRequest request) {

        refreshTokenService.revokeToken(request.refreshToken());

        return ResponseEntity.ok().build();
    }
}