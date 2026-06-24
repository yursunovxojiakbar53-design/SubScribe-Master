package com.example.d.subscription.repository;

import com.example.d.subscription.entity.Subscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
    List<Subscription> findByUser_Username(String userUsername);

    Page<Subscription> findByUser_IdAndIsDeleteFalse(Integer userId, Pageable pageable);

    List<Subscription> findAllBySetNextPaymentDate(LocalDate date);


    List<Subscription> findByUser_Id(Integer userId);


    List<Subscription> findByUser_IdAndIsDeleteFalse(Integer id);
}
