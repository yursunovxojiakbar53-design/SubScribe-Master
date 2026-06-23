package com.example.d.auth.dto;

import lombok.Getter;

public record RegisterDto(String username,String fullName,String email,String password) {
}
