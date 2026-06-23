package com.example.d.payment.mapper;

import com.example.d.payment.PaymentHistory;
import com.example.d.payment.dto.PaymentHistoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentHistoryMapper {
    @Mapping(source = "subscription.serviceName", target = "subscriptionName")
    PaymentHistoryResponse toResponse(PaymentHistory entity);
}
