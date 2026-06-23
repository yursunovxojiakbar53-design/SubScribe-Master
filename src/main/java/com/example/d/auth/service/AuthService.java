package com.example.d.auth.service;

import com.example.d.auth.dto.AuthResponse;
import com.example.d.auth.dto.LoginDto;
import com.example.d.auth.dto.RegisterDto;
import com.example.d.auth.entity.RefreshToken;
import com.example.d.user.repository.UserRepo;
import com.example.d.exception.AlreadyExistException;
import com.example.d.extra.ApiResponse;
import com.example.d.security.CustomUserDetails;
import com.example.d.user.entity.Users;
import com.example.d.user.enums.Role;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepo userRepo;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    public ApiResponse register(RegisterDto users) {
        if (userRepo.existsByUsername(users.username())) throw new AlreadyExistException("User already exists");
        int emailCode = 100000 + new Random().nextInt(900000);
        Users user = Users.builder()
                .username(users.username())
                .password(passwordEncoder.encode(users.password()))
                .fullName(users.fullName())
                .email(users.email())
                .role(Role.USER)
                .accountNonLocked(true)
                .notificationEnabled(true)
                .emailCode(emailCode)
                .build();
        userRepo.save(user);
        emailService.sendVerificationEmail(users.email(), emailCode);
        return new ApiResponse("User registered successfully", true);
    }


    @Transactional
    public AuthResponse login(LoginDto request) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        Users user = userRepo.findByEmail(request.username()).orElseThrow();
        UserDetails userDetails= new CustomUserDetails(user);
        String accessToken = jwtService.generateToken(userDetails);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .build();
    }


    public ApiResponse verifyEmail(String email, Integer code) {
        Users user = userRepo.findByEmail(email).orElse(null);
        if (user == null) return new ApiResponse("Not found", false);
        if (!code.equals(user.getEmailCode())) {
            return new ApiResponse("Wrong email code", false);
        }

        user.setEnabled(true);
        user.setEmailCode(null);

        userRepo.save(user);
        return new ApiResponse("Email tasdiqlandi! Endi login qiling.", true);
    }

    public ApiResponse verify(String email, Integer code) {
        return verifyEmail(email, code);
    }

}
