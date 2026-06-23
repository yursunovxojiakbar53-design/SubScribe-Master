package com.example.d.user.service;

import com.example.d.exception.ForbiddenException;
import com.example.d.exception.NotFoundException;
import com.example.d.extra.ApiResponse;
import com.example.d.security.CustomUserDetails;
import com.example.d.subscription.dto.SubscriptionResponse;
import com.example.d.subscription.entity.Subscription;
import com.example.d.user.dto.UserResponse;
import com.example.d.user.dto.UserUpdateResponse;
import com.example.d.user.entity.Users;
import com.example.d.user.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;


    public ApiResponse getUserById(Integer id) {
        Users user = userRepo.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        return new ApiResponse("User found",true,  toResponse(user));
    }


    public Page<UserResponse> getUsers(int page, int size) {
       int safeSize = Math.min(size, 50);
       Pageable pageable = PageRequest.of(page, safeSize);
       return userRepo.findAll(pageable).map(this::toResponse);
    }


    public  ApiResponse deleteUser(Integer id, Authentication authentication) {
        Users user = userRepo.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        String userName = getUser(authentication);
        if (!user.getUsername().equals(userName)) {
            throw new ForbiddenException("You are not allowed to delete this user");
        }
        userRepo.delete(user);
        return new ApiResponse("User deleted successfully", true, null);
    }


    public ApiResponse updateUser(Integer id, UserUpdateResponse dto, Authentication authentication) {
        Users user = userRepo.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        user.setPassword(passwordEncoder.encode(dto.password()));
        if (dto.username() != null) user.setUsername(dto.username());
        if (dto.email() != null) user.setEmail(dto.email());
        if (dto.fullName() != null) user.setFullName(dto.fullName());
        userRepo.save(user);
        return new ApiResponse("User updated successfully", true, toResponse(user));
    }



    public SubscriptionResponse toSubscriptionResponse(Subscription subscription) {
        return new SubscriptionResponse(
                subscription.getId(),
                subscription.getServiceName(),
                subscription.getAmount()
        );
    }

    public UserResponse toResponse(Users users) {
        List<SubscriptionResponse> subs =
                users.getSubscriptions()
                        .stream()
                        .map(this::toSubscriptionResponse)
                        .toList();
        return new UserResponse(users.getId(), users.getUsername(), subs);
    }

    public String getUser(org.springframework.security.core.Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new ForbiddenException("Invalid user");
        }
        return userDetails.getUsername();
    }
}
