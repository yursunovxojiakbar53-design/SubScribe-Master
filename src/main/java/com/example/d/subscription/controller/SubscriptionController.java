package com.example.d.subscription.controller;

import com.example.d.extra.ApiResponse;
import com.example.d.subscription.dto.SubscriptionCreateRequest;
import com.example.d.subscription.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscription")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;


    @PostMapping
    public ResponseEntity<?> addSubscription(@Valid @RequestBody SubscriptionCreateRequest subscription, Authentication authentication) {
         ApiResponse apiResponse=subscriptionService.addSubscription(subscription,authentication);
         return ResponseEntity.ok(apiResponse);
    }
    @GetMapping
    public ResponseEntity<?> getSubscriptions(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, Authentication authentication) {
         ApiResponse apiResponse=subscriptionService.getSubscription(page,size,authentication);
         return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSubscription(@PathVariable Integer id, @RequestBody SubscriptionCreateRequest subscription, Authentication authentication) {
         ApiResponse apiResponse=subscriptionService.updateSubscription(id,subscription,authentication);
         return ResponseEntity.ok(apiResponse);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubscription(@PathVariable Integer id, Authentication authentication) {
         ApiResponse apiResponse=subscriptionService.deleteSubscription(id,authentication);
         return ResponseEntity.ok(apiResponse);
    }


}
