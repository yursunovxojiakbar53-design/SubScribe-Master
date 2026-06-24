package com.example.d.report.service;

import com.example.d.currency.service.CurrencyService;
import com.example.d.exception.NotFoundException;
import com.example.d.payment.PaymentHistory;
import com.example.d.payment.repository.PaymentHistoryRepository;
import com.example.d.report.dto.SubscriptionReportRow;
import com.example.d.report.generator.CsvGenerator;
import com.example.d.report.generator.ExcelGenerator;
import com.example.d.security.SecurityUtils;
import com.example.d.subscription.entity.Subscription;
import com.example.d.subscription.repository.SubscriptionRepository;
import com.example.d.user.entity.Users;
import com.example.d.user.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelReportService implements ReportService {

    private final SubscriptionRepository subscriptionRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final UserRepo userRepo;
    private final CurrencyService currencyService;
    private final SecurityUtils securityUtils;
    private final ExcelGenerator excelGenerator;
    private final CsvGenerator csvGenerator;

    @Override
    @Transactional(readOnly = true)
    public byte[] generateExcelReport(Authentication authentication) {
        return excelGenerator.generate(buildReportRows(authentication));
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateCsvReport(Authentication authentication) {
        return csvGenerator.generate(buildReportRows(authentication));
    }

    /**
     * Foydalanuvchining o'chirilmagan barcha obunalari uchun hisobot qatorlarini tayyorlaydi.
     */
    private List<SubscriptionReportRow> buildReportRows(Authentication authentication) {
        String username = securityUtils.getUsername(authentication);
        Users user = userRepo.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<Subscription> subscriptions =
                subscriptionRepository.findByUser_IdAndIsDeleteFalse(user.getId());

        return subscriptions.stream()
                .map(subscription -> toReportRow(subscription, user))
                .toList();
    }

    private SubscriptionReportRow toReportRow(Subscription subscription, Users user) {
        BigDecimal equivalent = currencyService.convert(
                subscription.getAmount(), subscription.getCurrency(), user.getPreferredCurrency()
        );

        List<PaymentHistory> allPayments = paymentHistoryRepository
                .findBySubscription_IdOrderByPaymentDateDesc(subscription.getId());

        BigDecimal monthlyTotal = sumPaymentsSince(allPayments, LocalDate.now().minusMonths(1));
        BigDecimal yearlyTotal = sumPaymentsSince(allPayments, LocalDate.now().minusYears(1));

        return new SubscriptionReportRow(
                subscription.getServiceName(),
                subscription.getAmount(),
                subscription.getCurrency(),
                user.getPreferredCurrency(),
                equivalent,
                monthlyTotal,
                yearlyTotal
        );
    }




    /**
     * Berilgan sanadan beri sodir bo'lgan HAQIQIY to'lovlarni yig'indisini qaytaradi.
     * Har bir to'lov - o'z vaqtidagi haqiqiy kursi bilan saqlangan
     * (PaymentHistory.amountInBaseCurrency). Hech qanday taxminiy qiymat
     * qo'shilmaydi - agar shu davrda to'lov bo'lmagan bo'lsa, natija 0 bo'ladi.
     */
    private BigDecimal sumPaymentsSince(List<PaymentHistory> payments, LocalDate since) {
        return payments.stream()
                .filter(p -> !p.getPaymentDate().isBefore(since))
                .map(PaymentHistory::getAmountInBaseCurrency)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }
}