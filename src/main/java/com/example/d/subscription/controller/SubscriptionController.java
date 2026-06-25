package com.example.d.subscription.controller;

import com.example.d.extra.ApiResponse;
import com.example.d.extra.Perms;
import com.example.d.extra.RequirePermission;
import com.example.d.subscription.dto.SubscriptionCreateRequest;
import com.example.d.subscription.enums.CurrencyType;
import com.example.d.subscription.enums.SubscriptionStatus;
import com.example.d.subscription.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/subscription")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;


    @PostMapping
    @RequirePermission(Perms.SUBSCRIPTION_CREATE)
    public ResponseEntity<?> addSubscription(@Valid @RequestBody SubscriptionCreateRequest subscription, Authentication authentication) {
         ApiResponse apiResponse=subscriptionService.addSubscription(subscription,authentication);
         return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    @RequirePermission(Perms.SUBSCRIPTION_READ)
    public ResponseEntity<?> getSubscriptions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) SubscriptionStatus status,
            @RequestParam(required = false) CurrencyType currency,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            Authentication authentication) {
         ApiResponse apiResponse =
                 subscriptionService.getSubscription(page, size, status, currency, minPrice, maxPrice, authentication);
         return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    @RequirePermission(Perms.SUBSCRIPTION_UPDATE)
    public ResponseEntity<?> updateSubscription(@PathVariable Integer id, @Valid @RequestBody SubscriptionCreateRequest subscription, Authentication authentication) {
         ApiResponse apiResponse=subscriptionService.updateSubscription(id,subscription,authentication);
         return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/{id}/status")
    @RequirePermission(Perms.SUBSCRIPTION_UPDATE)
    public ResponseEntity<?> changeStatus(@PathVariable Integer id, @RequestParam SubscriptionStatus status, Authentication authentication) {
         ApiResponse apiResponse=subscriptionService.changeStatus(id, status, authentication);
         return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    @RequirePermission(Perms.SUBSCRIPTION_DELETE)
    public ResponseEntity<?> deleteSubscription(@PathVariable Integer id, Authentication authentication) {
         ApiResponse apiResponse=subscriptionService.deleteSubscription(id,authentication);
         return ResponseEntity.ok(apiResponse);
    }


}
