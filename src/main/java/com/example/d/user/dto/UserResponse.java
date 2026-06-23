package com.example.d.user.dto;

import com.example.d.subscription.dto.SubscriptionResponse;

import java.util.List;

public record UserResponse(Integer id, String username, List<SubscriptionResponse> subscriptions) {
}
