package com.example.d.payment.controller;


import com.example.d.extra.Perms;
import com.example.d.extra.RequirePermission;
import com.example.d.payment.service.PaymentHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payment-history")
@RequiredArgsConstructor
@Tag(name = "Payment History", description = "Obunalar bo'yicha to'lov tarixi")
public class PaymentHistoryController {
    private final PaymentHistoryService paymentHistoryService;

    @GetMapping("/subscription/{subscriptionId}")
    @RequirePermission(Perms.PAYMENT_READ)
    @Operation(summary = "Obuna to'lov tarixi")
    public ResponseEntity<?> getHistory(@PathVariable Integer subscriptionId, Authentication authentication) {
        return ResponseEntity.ok(paymentHistoryService.getHistoryBySubscription(subscriptionId, authentication));
    }
}
