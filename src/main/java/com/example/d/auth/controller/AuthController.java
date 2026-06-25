package com.example.d.auth.controller;

import com.example.d.auth.dto.AuthResponse;
import com.example.d.auth.dto.LoginDto;
import com.example.d.auth.dto.RegisterDto;
import com.example.d.auth.service.AuthService;
import com.example.d.extra.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/auth", "/auth"})
public class AuthController {
     private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterDto users) {
        ApiResponse response = authService.register(users);
        return ResponseEntity.status(response.isStatus() ? 200 : 409).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDto loginDto) {
        AuthResponse response = authService.login(loginDto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam String email, @RequestParam Integer code) {
        ApiResponse response = authService.verify(email, code);
        return ResponseEntity.status(response.isStatus() ? 200 : 409).body(response);
    }

}
