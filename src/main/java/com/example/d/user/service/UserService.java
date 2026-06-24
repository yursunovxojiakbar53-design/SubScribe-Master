package com.example.d.user.service;

import com.example.d.currency.service.CurrencyService;
import com.example.d.exception.ForbiddenException;
import com.example.d.exception.NotFoundException;
import com.example.d.extra.ApiResponse;
import com.example.d.security.SecurityUtils;
import com.example.d.subscription.dto.SubscriptionResponse;
import com.example.d.subscription.entity.Subscription;
import com.example.d.subscription.enums.CurrencyType;
import com.example.d.user.dto.UserResponse;
import com.example.d.user.dto.UserUpdateResponse;
import com.example.d.user.entity.Users;
import com.example.d.user.repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
private final SecurityUtils securityUtils;
private final CurrencyService currencyService;

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
        String userName = securityUtils.getUsername(authentication);
        if (!user.getUsername().equals(userName)) {
            throw new ForbiddenException("You are not allowed to delete this user");
        }
        userRepo.delete(user);
        return new ApiResponse("User deleted successfully", true, null);
    }


    public ApiResponse updateUser(Integer id, UserUpdateResponse dto, Authentication authentication) {
        Users user = userRepo.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        String username = securityUtils.getUsername(authentication);
        if (!user.getUsername().equals(username)) {
            throw new ForbiddenException("You are not allowed to update this user");
        }
        user.setPassword(passwordEncoder.encode(dto.password()));
        if (dto.username() != null) user.setUsername(dto.username());
        if (dto.email() != null) user.setEmail(dto.email());
        if (dto.fullName() != null) user.setFullName(dto.fullName());
        userRepo.save(user);
        return new ApiResponse("User updated successfully", true, toResponse(user));
    }


    public SubscriptionResponse toSubscriptionResponse(Subscription subscription, BigDecimal equivalentInBaseCurrency) {
        return new SubscriptionResponse(
                subscription.getId(),
                subscription.getServiceName(),
                subscription.getAmount(),
                subscription.getCurrency(),
                equivalentInBaseCurrency,           // tashqaridan hisoblanib keladi (CurrencyService orqali)
                subscription.getStartDate(),
                subscription.getSetNextPaymentDate(),
                subscription.getBillingCycle(),
                subscription.getStatus()
        );
    }


    public UserResponse toResponse(Users users) {
        List<SubscriptionResponse> subs =
                users.getSubscriptions()
                        .stream()
                        .map(sub -> {
                            BigDecimal equivalent = currencyService.convert(
                                    sub.getAmount(),
                                    sub.getCurrency(),
                                    CurrencyType.UZS);
                            return toSubscriptionResponse(sub, equivalent);})
                        .toList();
        return new UserResponse(users.getId(), users.getUsername(), subs);
    }



    @Transactional
    public ApiResponse updatePreferredCurrency(CurrencyType currency, Authentication authentication) {
        String username = securityUtils.getUsername(authentication);
        Users user = userRepo.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));

        user.setPreferredCurrency(currency);
        userRepo.save(user);

        return new ApiResponse("Preferred currency updated successfully", true);
    }
}
