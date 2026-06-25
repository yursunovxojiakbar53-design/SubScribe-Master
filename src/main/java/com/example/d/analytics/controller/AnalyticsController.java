package com.example.d.analytics.controller;

import com.example.d.analytics.service.AnalyticsService;
import com.example.d.extra.ApiResponse;
import com.example.d.extra.Perms;
import com.example.d.extra.RequirePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Statistika: xarajatlar, dinamika, kategoriyalar, reyting")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/most-expensive")
    @RequirePermission(Perms.ANALYTICS_READ)
    @Operation(summary = "Eng qimmat obuna (CURRENT_MONTH / OVERALL)")
    public ResponseEntity<ApiResponse> getMostExpensive(@RequestParam(defaultValue = "OVERALL") String period, Authentication authentication) {
        boolean isCurrentMonth = "CURRENT_MONTH".equalsIgnoreCase(period);
        return ResponseEntity.ok(analyticsService.getMostExpensive(isCurrentMonth, authentication));
    }

    @GetMapping("/monthly-spending")
    @RequirePermission(Perms.ANALYTICS_READ)
    @Operation(summary = "Oylik umumiy xarajat")
    public ResponseEntity<ApiResponse> getMonthlySpending(Authentication authentication) {
        return ResponseEntity.ok(analyticsService.getMonthlySpending(authentication));
    }

    /**
     * GET /api/v1/analytics/trend?months=6
     */
    @GetMapping("/trend")
    @RequirePermission(Perms.ANALYTICS_READ)
    @Operation(summary = "Oylar bo'yicha dinamika (oxirgi N oy)")
    public ResponseEntity<ApiResponse> getMonthlyTrend(@RequestParam(defaultValue = "6") int months, Authentication authentication) {
        return ResponseEntity.ok(analyticsService.getMonthlyTrend(months, authentication));
    }

    @GetMapping("/by-category")
    @RequirePermission(Perms.ANALYTICS_READ)
    @Operation(summary = "Kategoriya bo'yicha taqsimot")
    public ResponseEntity<ApiResponse> getByCategory(Authentication authentication) {
        return ResponseEntity.ok(analyticsService.getByCategoryBreakdown(authentication));
    }

    /**
     * ADMIN: tizimdagi barcha foydalanuvchilar bo'yicha eng ko'p ishlatiladigan xizmatlar reytingi.
     * GET /api/v1/analytics/admin/popular-services?limit=10
     */
    @GetMapping("/admin/popular-services")
    @RequirePermission(Perms.ADMIN_ANALYTICS_READ)
    @Operation(summary = "Eng ko'p ishlatilgan xizmatlar reytingi (admin)")
    public ResponseEntity<ApiResponse> getMostUsedServices(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(analyticsService.getMostUsedServices(limit));
    }
}