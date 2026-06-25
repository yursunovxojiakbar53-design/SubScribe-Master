package com.example.d.payment.service;

import com.example.d.currency.service.CurrencyService;
import com.example.d.exception.ForbiddenException;
import com.example.d.exception.NotFoundException;
import com.example.d.extra.ApiResponse;
import com.example.d.payment.PaymentHistory;
import com.example.d.payment.dto.PaymentHistoryResponse;
import com.example.d.payment.mapper.PaymentHistoryMapper;
import com.example.d.payment.repository.PaymentHistoryRepository;
import com.example.d.security.CustomUserDetails;
import com.example.d.subscription.entity.Subscription;
import com.example.d.subscription.enums.CurrencyType;
import com.example.d.subscription.repository.SubscriptionRepository;
import com.example.d.user.entity.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentHistoryService unit testlari")
class PaymentHistoryServiceTest {

    @Mock
    private PaymentHistoryRepository paymentHistoryRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private PaymentHistoryMapper paymentHistoryMapper;

    @Mock
    private CurrencyService currencyService;

    @InjectMocks
    private PaymentHistoryService paymentHistoryService;

    private Users owner;
    private Subscription subscription;

    @BeforeEach
    void setUp() {
        owner = Users.builder()
                .username("john")
                .preferredCurrency(CurrencyType.UZS)
                .build();

        subscription = Subscription.builder()
                .id(1)
                .serviceName("Netflix")
                .amount(new BigDecimal("10.00"))
                .currency(CurrencyType.USD)
                .user(owner)
                .build();
    }

    // ==================== recordPayment ====================

    @Test
    @DisplayName("recordPayment: summa va valyuta obunaning o'z valyutasida saqlanadi, ekvivalent hisoblanadi")
    void recordPayment_savesHistoryWithSubscriptionCurrencyAndComputedEquivalent() {
        LocalDate paymentDate = LocalDate.of(2026, 6, 25);

        // 1 USD -> 12 500 UZS,  10 USD -> 125 000 UZS
        when(currencyService.convert(eq(BigDecimal.ONE), eq(CurrencyType.USD), eq(CurrencyType.UZS)))
                .thenReturn(new BigDecimal("12500.00"));
        when(currencyService.convert(eq(new BigDecimal("10.00")), eq(CurrencyType.USD), eq(CurrencyType.UZS)))
                .thenReturn(new BigDecimal("125000.00"));

        paymentHistoryService.recordPayment(subscription, paymentDate);

        ArgumentCaptor<PaymentHistory> captor = ArgumentCaptor.forClass(PaymentHistory.class);
        verify(paymentHistoryRepository).save(captor.capture());
        PaymentHistory saved = captor.getValue();

        assertThat(saved.getSubscription()).isSameAs(subscription);
        assertThat(saved.getAmount()).isEqualByComparingTo("10.00");
        // valyuta obunaning O'Z valyutasi (USD), preferredCurrency (UZS) EMAS
        assertThat(saved.getCurrency()).isEqualTo(CurrencyType.USD);
        assertThat(saved.getPaymentDate()).isEqualTo(paymentDate);
        assertThat(saved.getExchangeRateAtPayment()).isEqualByComparingTo("12500.00");
        assertThat(saved.getAmountInBaseCurrency()).isEqualByComparingTo("125000.00");
    }

    @Test
    @DisplayName("recordPayment: asosiy valyuta obuna valyutasi bilan bir xil bo'lsa ekvivalent o'zgarmaydi")
    void recordPayment_sameCurrencyKeepsAmount() {
        owner.setPreferredCurrency(CurrencyType.USD);
        LocalDate paymentDate = LocalDate.of(2026, 6, 25);

        when(currencyService.convert(eq(BigDecimal.ONE), eq(CurrencyType.USD), eq(CurrencyType.USD)))
                .thenReturn(BigDecimal.ONE);
        when(currencyService.convert(eq(new BigDecimal("10.00")), eq(CurrencyType.USD), eq(CurrencyType.USD)))
                .thenReturn(new BigDecimal("10.00"));

        paymentHistoryService.recordPayment(subscription, paymentDate);

        ArgumentCaptor<PaymentHistory> captor = ArgumentCaptor.forClass(PaymentHistory.class);
        verify(paymentHistoryRepository).save(captor.capture());

        assertThat(captor.getValue().getAmountInBaseCurrency()).isEqualByComparingTo("10.00");
        assertThat(captor.getValue().getExchangeRateAtPayment()).isEqualByComparingTo("1");
    }

    // ==================== getHistoryBySubscription ====================

    @Test
    @DisplayName("getHistoryBySubscription: egasi so'raganda tarix qaytadi")
    void getHistory_returnsMappedHistoryForOwner() {
        PaymentHistoryResponse response = new PaymentHistoryResponse();
        PaymentHistory entity = new PaymentHistory();

        when(subscriptionRepository.findById(1)).thenReturn(Optional.of(subscription));
        when(paymentHistoryRepository.findBySubscription_IdOrderByPaymentDateDesc(1))
                .thenReturn(List.of(entity));
        when(paymentHistoryMapper.toResponse(entity)).thenReturn(response);

        ApiResponse result = paymentHistoryService.getHistoryBySubscription(1, authFor(owner));

        assertThat(result.isStatus()).isTrue();
        assertThat(result.getMessage()).isEqualTo("Payment history retrieved successfully");
        assertThat(result.getData()).isInstanceOf(List.class);

        @SuppressWarnings("unchecked")
        List<PaymentHistoryResponse> data = (List<PaymentHistoryResponse>) result.getData();
        assertThat(data).containsExactly(response);
    }

    @Test
    @DisplayName("getHistoryBySubscription: obuna topilmasa NotFoundException")
    void getHistory_throwsWhenSubscriptionMissing() {
        Authentication auth = org.mockito.Mockito.mock(Authentication.class);
        when(subscriptionRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentHistoryService.getHistoryBySubscription(99, auth))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Subscription not found");

        verifyNoInteractions(paymentHistoryRepository, paymentHistoryMapper);
    }

    @Test
    @DisplayName("getHistoryBySubscription: boshqa foydalanuvchi so'rasa ForbiddenException")
    void getHistory_throwsWhenNotOwner() {
        Users stranger = Users.builder().username("alice").preferredCurrency(CurrencyType.UZS).build();

        when(subscriptionRepository.findById(1)).thenReturn(Optional.of(subscription));

        assertThatThrownBy(() -> paymentHistoryService.getHistoryBySubscription(1, authFor(stranger)))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("User not allowed to view this history");

        verifyNoInteractions(paymentHistoryMapper);
    }

    @Test
    @DisplayName("getHistoryBySubscription: principal CustomUserDetails bo'lmasa ForbiddenException")
    void getHistory_throwsWhenPrincipalInvalid() {
        Authentication auth = org.mockito.Mockito.mock(Authentication.class);
        when(subscriptionRepository.findById(1)).thenReturn(Optional.of(subscription));
        when(auth.getPrincipal()).thenReturn("not-a-user-details");

        assertThatThrownBy(() -> paymentHistoryService.getHistoryBySubscription(1, auth))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("Invalid user");
    }

    // ==================== helper ====================

    private Authentication authFor(Users user) {
        Authentication auth = org.mockito.Mockito.mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(new CustomUserDetails(user));
        return auth;
    }
}
