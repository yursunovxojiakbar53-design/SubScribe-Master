package com.example.d.analytics;

import com.example.d.analytics.service.AnalyticsService;
import com.example.d.extra.ApiResponse;
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
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/most-expensive")
    public ResponseEntity<ApiResponse> getMostExpensive(@RequestParam(defaultValue = "OVERALL") String period, Authentication authentication) {

        boolean isCurrentMonth = "CURRENT_MONTH".equalsIgnoreCase(period);
        return ResponseEntity.ok(analyticsService.getMostExpensive(isCurrentMonth, authentication));
    }

    @GetMapping("/monthly-spending")
    public ResponseEntity<ApiResponse> getMonthlySpending(Authentication authentication) {
        return ResponseEntity.ok(analyticsService.getMonthlySpending(authentication));
    }

    /**
     * GET /api/v1/analytics/trend?months=6
     */
    @GetMapping("/trend")
    public ResponseEntity<ApiResponse> getMonthlyTrend(@RequestParam(defaultValue = "6") int months, Authentication authentication) {
        return ResponseEntity.ok(analyticsService.getMonthlyTrend(months, authentication));
    }

    @GetMapping("/by-category")
    public ResponseEntity<ApiResponse> getByCategory(Authentication authentication) {
        return ResponseEntity.ok(analyticsService.getByCategoryBreakdown(authentication));
    }
}