package com.example.d.subscription.repository;

import com.example.d.analytics.dto.PopularServiceItem;
import com.example.d.subscription.entity.Subscription;
import com.example.d.subscription.enums.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer>,
        JpaSpecificationExecutor<Subscription> {
    List<Subscription> findByUser_Username(String userUsername);

    /**
     * Tizimdagi eng ko'p ishlatiladigan xizmatlar reytingi (barcha foydalanuvchilar bo'yicha,
     * soft-delete qilinmaganlar). Native SQL emas - JPQL + constructor expression.
     */
    @Query("""
            SELECT new com.example.d.analytics.dto.PopularServiceItem(s.serviceName, COUNT(s))
            FROM Subscription s
            WHERE s.isDelete = false
            GROUP BY s.serviceName
            ORDER BY COUNT(s) DESC
            """)
    List<PopularServiceItem> findMostUsedServices(Pageable pageable);

    Page<Subscription> findByUser_IdAndIsDeleteFalse(Integer userId, Pageable pageable);

    List<Subscription> findAllBySetNextPaymentDate(LocalDate date);

    List<Subscription> findByStatusAndIsDeleteFalseAndSetNextPaymentDateLessThanEqual(
            SubscriptionStatus status, LocalDate date);


    List<Subscription> findByUser_Id(Integer userId);


    List<Subscription> findByUser_IdAndIsDeleteFalse(Integer id);
}
