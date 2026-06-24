package com.example.d.subscription.mapper;

import com.example.d.subscription.dto.SubscriptionCreateRequest;
import com.example.d.subscription.dto.SubscriptionResponse;
import com.example.d.subscription.entity.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {
    Subscription toEntity(SubscriptionCreateRequest request);

    @Mapping(target = "equivalentInBaseCurrency", ignore = true)
    SubscriptionResponse toResponse(Subscription entity);

    void updateEntityFromDto(SubscriptionCreateRequest request, @MappingTarget Subscription entity);

}
