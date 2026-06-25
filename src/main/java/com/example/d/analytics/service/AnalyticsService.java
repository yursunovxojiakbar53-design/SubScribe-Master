package com.example.d.analytics.service;

import com.example.d.analytics.dto.CategoryBreakdownItem;
import com.example.d.analytics.dto.MonthlySpendingResponse;
import com.example.d.analytics.dto.MonthlyTrendPoint;
import com.example.d.analytics.dto.MostExpensiveSubscriptionResponse;
import com.example.d.analytics.dto.PopularServiceItem;
import com.example.d.analytics.enums.SubscriptionCategory;
import com.example.d.currency.service.CurrencyService;
import com.example.d.exception.NotFoundException;
import com.example.d.extra.ApiResponse;
import com.example.d.payment.PaymentHistory;
import com.example.d.payment.repository.PaymentHistoryRepository;
import com.example.d.security.SecurityUtils;
import com.example.d.subscription.entity.Subscription;
import com.example.d.subscription.enums.SubscriptionStatus;
import com.example.d.subscription.repository.SubscriptionRepository;
import com.example.d.user.entity.Users;
import com.example.d.user.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    private final SubscriptionRepository subscriptionRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final UserRepo userRepo;
    private final CurrencyService currencyService;
    private final SecurityUtils securityUtils;


    @Transactional(readOnly = true)
    public ApiResponse getMostExpensive(boolean isCurrentMonth, Authentication authentication) {

        Users user = getUser(authentication);

        if (isCurrentMonth) {
            return new ApiResponse("Joriy oydagi eng qimmat obuna", true, getMostExpensiveCurrentMonth(user));
        }
        return new ApiResponse("Umumiy eng qimmat obuna", true,
                getMostExpensiveOverall(user));
    }

    private MostExpensiveSubscriptionResponse getMostExpensiveCurrentMonth(Users user) {
        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate today = LocalDate.now();

        List<PaymentHistory> payments = paymentHistoryRepository.findBySubscription_User_IdAndPaymentDateBetween(user.getId(), firstDayOfMonth, today);

        return payments.stream()
                .max(Comparator.comparing(PaymentHistory::getAmountInBaseCurrency))
                .map(p -> new MostExpensiveSubscriptionResponse(
                        p.getSubscription().getServiceName(),
                        p.getAmountInBaseCurrency(),
                        user.getPreferredCurrency()
                ))
                .orElse(null);
    }

    private MostExpensiveSubscriptionResponse getMostExpensiveOverall(Users user) {
        List<Subscription> activeSubscriptions = subscriptionRepository.findByUser_IdAndIsDeleteFalse(user.getId())
                .stream()
                .filter(s -> s.getStatus() == SubscriptionStatus.ACTIVE)
                .toList();

        return activeSubscriptions.stream()
                .map(s -> Map.entry(s, currencyService.convert(
                        s.getAmount(), s.getCurrency(), user.getPreferredCurrency())))
                .max(Map.Entry.comparingByValue())
                .map(entry -> new MostExpensiveSubscriptionResponse(
                        entry.getKey().getServiceName(),
                        entry.getValue(),
                        user.getPreferredCurrency()
                ))
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public ApiResponse getMonthlySpending(Authentication authentication) {
        Users user = getUser(authentication);

        List<Subscription> activeSubscriptions = subscriptionRepository.findByUser_IdAndIsDeleteFalse(user.getId())
                .stream()
                .filter(s -> s.getStatus() == SubscriptionStatus.ACTIVE)
                .toList();

        BigDecimal total = activeSubscriptions.stream()
                .map(s -> normalizeToMonthly(s, user))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        MonthlySpendingResponse response = new MonthlySpendingResponse(
                total, user.getPreferredCurrency(), activeSubscriptions.size());

        return new ApiResponse("Oylik umumiy xarajat hisoblandi", true, response);
    }

    private BigDecimal normalizeToMonthly(Subscription subscription, Users user) {
        BigDecimal equivalent = currencyService.convert(subscription.getAmount(), subscription.getCurrency(), user.getPreferredCurrency());

        return switch (subscription.getBillingCycle()) {
            case WEEKLY -> equivalent.multiply(BigDecimal.valueOf(4.33));
            case MONTHLY -> equivalent;
            case YEARLY -> equivalent.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
        };
    }

    // ====================  OYLAR BO'YICHA chart uchun   ====================

    @Transactional(readOnly = true)
    public ApiResponse getMonthlyTrend(int months, Authentication authentication) {
        Users user = getUser(authentication);

        LocalDate start = YearMonth.now().minusMonths(months - 1L).atDay(1);
        LocalDate end = LocalDate.now();

        List<PaymentHistory> payments = paymentHistoryRepository
                .findBySubscription_User_IdAndPaymentDateBetween(user.getId(), start, end);

        Map<String, BigDecimal> grouped = payments.stream()
                .collect(Collectors.groupingBy(p -> p.getPaymentDate().format(MONTH_FORMAT),
                        Collectors.reducing(BigDecimal.ZERO, PaymentHistory::getAmountInBaseCurrency, BigDecimal::add)
                ));

        List<MonthlyTrendPoint> trend = java.util.stream.Stream.iterate(YearMonth.from(start), ym -> ym.plusMonths(1))
                .limit(months)
                .map(ym -> {
                    String key = ym.format(MONTH_FORMAT);
                    BigDecimal total = grouped.getOrDefault(key, BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
                    return new MonthlyTrendPoint(key, total);
                })
                .toList();

        return new ApiResponse("Oylik dinamika hisoblandi", true, trend);
    }

    // ====================  KATEGORIYA BO'YICHA GURUHLASH ====================

    @Transactional(readOnly = true)
    public ApiResponse getByCategoryBreakdown(Authentication authentication) {
        Users user = getUser(authentication);

        List<Subscription> activeSubscriptions = subscriptionRepository
                .findByUser_IdAndIsDeleteFalse(user.getId())
                .stream()
                .filter(s -> s.getStatus() == SubscriptionStatus.ACTIVE)
                .toList();

        Map<SubscriptionCategory, List<Subscription>> grouped = activeSubscriptions.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getSubscriptionCategory() == null ? SubscriptionCategory.OTHER : s.getSubscriptionCategory(),
                        () -> new EnumMap<>(SubscriptionCategory.class),
                        Collectors.toList()
                ));

        List<CategoryBreakdownItem> result = grouped.entrySet().stream()
                .map(entry -> {
                    BigDecimal total = entry.getValue().stream()
                            .map(s -> normalizeToMonthly(s, user))
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .setScale(2, RoundingMode.HALF_UP);

                    return new CategoryBreakdownItem(entry.getKey(), total, entry.getValue().size());
                })
                .sorted(Comparator.comparing(CategoryBreakdownItem::totalInBaseCurrency).reversed())
                .toList();

        return new ApiResponse("Kategoriya bo'yicha taqsimot hisoblandi", true, result);
    }


    // ==================== ADMIN: ENG KO'P ISHLATILADIGAN XIZMATLAR ====================

    @Transactional(readOnly = true)
    public ApiResponse getMostUsedServices(int limit) {
        List<PopularServiceItem> ranking = subscriptionRepository.findMostUsedServices(PageRequest.of(0, limit));

        return new ApiResponse("Eng ko'p ishlatiladigan xizmatlar reytingi", true, ranking);
    }

    private Users getUser(Authentication authentication) {
        String username = securityUtils.getUsername(authentication);
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}