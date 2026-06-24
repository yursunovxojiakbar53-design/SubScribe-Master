package com.example.d.payment.repository;

import com.example.d.payment.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Integer> {

    List<PaymentHistory> findBySubscription_IdOrderByPaymentDateDesc(Integer subscriptionId);

    List<PaymentHistory> findBySubscription_User_IdAndPaymentDateBetween(
            Integer userId, LocalDate start, LocalDate end);

    List<PaymentHistory> findBySubscription_User_Id(Integer userId);


}