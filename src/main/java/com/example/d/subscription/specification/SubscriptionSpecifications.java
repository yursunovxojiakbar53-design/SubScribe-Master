package com.example.d.subscription.specification;

import com.example.d.subscription.entity.Subscription;
import com.example.d.subscription.enums.CurrencyType;
import com.example.d.subscription.enums.SubscriptionStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Obunalarni Specification (Criteria) API orqali filtrlash uchun.
 * Native SQL ishlatilmaydi. Egasi (userId) va soft-delete sharti har doim qo'llanadi,
 * qolgan filtrlar (status, valyuta, narx oralig'i) faqat berilgan bo'lsa qo'shiladi.
 */
public final class SubscriptionSpecifications {

    private SubscriptionSpecifications() {
    }

    public static Specification<Subscription> filter(
            Integer userId,
            SubscriptionStatus status,
            CurrencyType currency,
            BigDecimal minPrice,
            BigDecimal maxPrice
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("user").get("id"), userId));
            predicates.add(cb.isFalse(root.get("isDelete")));

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (currency != null) {
                predicates.add(cb.equal(root.get("currency"), currency));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("amount"), maxPrice));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
