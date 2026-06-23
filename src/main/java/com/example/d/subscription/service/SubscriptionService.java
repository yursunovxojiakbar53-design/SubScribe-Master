package com.example.d.subscription.service;

import com.example.d.exception.ForbiddenException;
import com.example.d.exception.NotFoundException;
import com.example.d.extra.ApiResponse;
import com.example.d.security.CustomUserDetails;
import com.example.d.subscription.dto.SubscriptionCreateRequest;
import com.example.d.subscription.dto.SubscriptionResponse;
import com.example.d.subscription.entity.Subscription;
import com.example.d.subscription.enums.BillingCycle;
import com.example.d.subscription.enums.SubscriptionStatus;
import com.example.d.subscription.mapper.SubscriptionMapper;
import com.example.d.subscription.repository.SubscriptionRepository;
import com.example.d.user.entity.Users;
import com.example.d.user.repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionMapper subscriptionMapper;
    private final UserRepo userRepo;
    private final SubscriptionRepository subscriptionRepository;

    public ApiResponse addSubscription(SubscriptionCreateRequest request, Authentication authentication) {
        String userName = getUser(authentication);
        Users user = userRepo.findByUsername(userName).orElseThrow(() -> new NotFoundException("User not found"));

        Subscription subscription = subscriptionMapper.toEntity(request);
        subscription.setUser(user);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setSetNextPaymentDate(calculateNextPaymentDate(subscription.getStartDate(), subscription.getBillingCycle()));
        user.getSubscriptions().add(subscription);
        subscriptionRepository.save(subscription);
        return new ApiResponse("Subscription added successfully", true);
    }


    public ApiResponse getSubscription(int page, int size, Authentication authentication) {
        Pageable pageable=  PageRequest.of(page,size);
        String userName = getUser(authentication);
        Users users = userRepo.findByUsername(userName).orElseThrow(() -> new NotFoundException("User not found"));
        Page<Subscription> subscriptions = subscriptionRepository.findByUser_IdAndIsDeleteFalse(users.getId(), pageable);
        Page<SubscriptionResponse> response = subscriptions.map(subscriptionMapper::toResponse);
        return new ApiResponse("Subscription retrieved successfully", true, response);
    }


    @Transactional
    public ApiResponse updateSubscription(Integer id, SubscriptionCreateRequest request, Authentication authentication) {
        Subscription subscription= subscriptionRepository.findById(id).orElseThrow(() -> new NotFoundException("Subscription not found"));
        String userName = getUser(authentication);
        if (!subscription.getUser().getUsername().equals(userName))throw new ForbiddenException("User not allowed to update subscription");

        subscriptionMapper.updateEntityFromDto(request, subscription);
        subscription.setSetNextPaymentDate(calculateNextPaymentDate(request.getStartDate(), request.getBillingCycle()));
        subscriptionRepository.save(subscription);
        return new ApiResponse("Subscription updated successfully", true, subscriptionMapper.toResponse(subscription));
    }


    @Transactional
    public ApiResponse deleteSubscription(Integer id, Authentication authentication) {
        Subscription subscription = subscriptionRepository.findById(id).orElseThrow(() -> new NotFoundException("Subscription not found"));
        String userName = getUser(authentication);
        if (!subscription.getUser().getUsername().equals(userName))
            throw new ForbiddenException("User not allowed to delete subscription");

        subscription.setIsDelete(true);
        subscriptionRepository.save(subscription);
        return new ApiResponse("Subscription deleted successfully", true);
    }








    private LocalDate calculateNextPaymentDate(LocalDate startDate, BillingCycle billingCycle) {
        return switch (billingCycle) {
            case WEEKLY -> startDate.plusWeeks(1);
            case MONTHLY -> startDate.plusMonths(1);
            case YEARLY -> startDate.plusYears(1);
        };
    }


    public String getUser(org.springframework.security.core.Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new ForbiddenException("Invalid user");
        }
        return userDetails.getUsername();
    }


}
