package com.example.d.subscription.service;

import com.example.d.currency.service.CurrencyService;
import com.example.d.exception.ForbiddenException;
import com.example.d.exception.NotFoundException;
import com.example.d.extra.ApiResponse;
import com.example.d.payment.service.PaymentHistoryService;
import com.example.d.security.SecurityUtils;
import com.example.d.subscription.dto.SubscriptionCreateRequest;
import com.example.d.subscription.dto.SubscriptionResponse;
import com.example.d.subscription.entity.Subscription;
import com.example.d.subscription.enums.BillingCycle;
import com.example.d.subscription.enums.CurrencyType;
import com.example.d.subscription.enums.SubscriptionStatus;
import com.example.d.subscription.mapper.SubscriptionMapper;
import com.example.d.subscription.repository.SubscriptionRepository;
import com.example.d.subscription.specification.SubscriptionSpecifications;
import com.example.d.user.entity.Users;
import com.example.d.user.repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionMapper subscriptionMapper;
    private final UserRepo userRepo;
    private final SubscriptionRepository subscriptionRepository;
    private final CurrencyService currencyService;
    private final SecurityUtils securityUtils;
    private final PaymentHistoryService paymentHistoryService;

    @Transactional
    public ApiResponse addSubscription(SubscriptionCreateRequest request, Authentication authentication) {
        String userName = securityUtils.getUsername(authentication);
        Users user = userRepo.findByUsername(userName).orElseThrow(() -> new NotFoundException("User not found"));

        Subscription subscription = subscriptionMapper.toEntity(request);
        subscription.setUser(user);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setIsDelete(false);

        subscriptionRepository.save(subscription);

        // startDate'dan bugungi kungacha bo'lgan barcha to'lovlar simulyatsiya qilinib
        // PaymentHistory'ga yoziladi (mavjud obunani jamlashda tarix darhol paydo bo'lishi uchun),
        // setNextPaymentDate esa kelajakdagi birinchi to'lov sanasiga o'rnatiladi.
        backfillPastPayments(subscription);

        return new ApiResponse("Subscription added successfully", true, buildResponse(subscription, user));
    }

    private SubscriptionResponse buildResponse(Subscription subscription, Users user) {
        SubscriptionResponse base = subscriptionMapper.toResponse(subscription);

        BigDecimal equivalent = currencyService.convert(subscription.getAmount(), subscription.getCurrency(), user.getPreferredCurrency());

        return new SubscriptionResponse(
                base.id(), base.serviceName(), base.price(), base.currency(),
                equivalent, base.startDate(), base.nextPaymentDate(),
                base.billingCycle(), base.status()
        );
    }


    public ApiResponse getSubscription(int page, int size, SubscriptionStatus status, CurrencyType currency,
                                       BigDecimal minPrice, BigDecimal maxPrice, Authentication authentication) {
        Pageable pageable = PageRequest.of(page, size);
        String userName = securityUtils.getUsername(authentication);
        Users users = userRepo.findByUsername(userName).orElseThrow(() -> new NotFoundException("User not found"));

        Specification<Subscription> spec =
                SubscriptionSpecifications.filter(users.getId(), status, currency, minPrice, maxPrice);

        Page<Subscription> subscriptions = subscriptionRepository.findAll(spec, pageable);
        Page<SubscriptionResponse> response = subscriptions.map(sub -> buildResponse(sub, users));
        return new ApiResponse("Subscription retrieved successfully", true, response);
    }


    @Transactional
    public ApiResponse updateSubscription(Integer id, SubscriptionCreateRequest request, Authentication authentication) {
        Subscription subscription = subscriptionRepository.findById(id).orElseThrow(() -> new NotFoundException("Subscription not found"));
        String userName = securityUtils.getUsername(authentication);
        if (!subscription.getUser().getUsername().equals(userName))
            throw new ForbiddenException("User not allowed to update subscription");

        subscriptionMapper.updateEntityFromDto(request, subscription);
        subscription.setNextPaymentDate(calculateNextPaymentDate(request.getStartDate(), request.getBillingCycle()));
        subscriptionRepository.save(subscription);
        return new ApiResponse("Subscription updated successfully", true, subscriptionMapper.toResponse(subscription));
    }


    @Transactional
    public ApiResponse deleteSubscription(Integer id, Authentication authentication) {
        Subscription subscription = subscriptionRepository.findById(id).orElseThrow(() -> new NotFoundException("Subscription not found"));
        String userName = securityUtils.getUsername(authentication);
        if (!subscription.getUser().getUsername().equals(userName))
            throw new ForbiddenException("User not allowed to delete subscription");

        subscription.setIsDelete(true);
        subscriptionRepository.save(subscription);
        return new ApiResponse("Subscription deleted successfully", true);
    }


    @Transactional
    public ApiResponse changeStatus(Integer id, SubscriptionStatus newStatus, Authentication authentication) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Subscription not found"));
        String userName = securityUtils.getUsername(authentication);
        if (!subscription.getUser().getUsername().equals(userName)) {
            throw new ForbiddenException("User not allowed to change this subscription");
        }

        subscription.setStatus(newStatus);
        subscriptionRepository.save(subscription);
        return new ApiResponse("Subscription status updated to " + newStatus, true,
                subscriptionMapper.toResponse(subscription));
    }

    /**
     * To'lov muddati kelgan (yoki o'tib ketgan) aktiv obunalar uchun to'lovni
     * PaymentHistory'ga yozadi va keyingi to'lov sanasini bir davrga suradi.
     * Scheduler tomonidan chaqiriladi.
     */
    @Transactional
    public void processDuePayments() {
        LocalDate today = LocalDate.now();
        List<Subscription> due = subscriptionRepository
                .findByStatusAndIsDeleteFalseAndSetNextPaymentDateLessThanEqual(SubscriptionStatus.ACTIVE, today);

        for (Subscription subscription : due) {
            paymentHistoryService.recordPayment(subscription, subscription.getNextPaymentDate());
            subscription.setNextPaymentDate(
                    calculateNextPaymentDate(subscription.getNextPaymentDate(), subscription.getBillingCycle()));
        }
        subscriptionRepository.saveAll(due);
    }

    /**
     * Obuna {@code startDate}'idan bugungi kungacha bo'lgan har bir to'lov sanasini
     * simulyatsiya qilib PaymentHistory'ga yozadi va keyingi to'lov sanasini (kelajakdagi
     * birinchi sana) o'rnatadi. Agar startDate kelajakda bo'lsa, to'lov yozilmaydi va
     * setNextPaymentDate = startDate bo'ladi.
     */
    private void backfillPastPayments(Subscription subscription) {
        LocalDate today = LocalDate.now();
        LocalDate paymentDate = subscription.getStartDate();

        while (!paymentDate.isAfter(today)) {
            paymentHistoryService.recordPayment(subscription, paymentDate);
            paymentDate = calculateNextPaymentDate(paymentDate, subscription.getBillingCycle());
        }

        subscription.setNextPaymentDate(paymentDate);
        subscriptionRepository.save(subscription);
    }

    private LocalDate calculateNextPaymentDate(LocalDate startDate, BillingCycle billingCycle) {
        return switch (billingCycle) {
            case WEEKLY -> startDate.plusWeeks(1);
            case MONTHLY -> startDate.plusMonths(1);
            case YEARLY -> startDate.plusYears(1);
        };
    }
    public List<Subscription> findSubscriptionsWithPaymentInDays(int days) {

        LocalDate targetDate = LocalDate.now().plusDays(days);

        return subscriptionRepository.findAllBySetNextPaymentDate(targetDate);
    }




}
