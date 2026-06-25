package com.example.d.user.enums;

import lombok.Getter;

@Getter
public enum Permission {

    SUBSCRIPTION_CREATE("subscription:create"),
    SUBSCRIPTION_READ("subscription:read"),
    SUBSCRIPTION_UPDATE("subscription:update"),
    SUBSCRIPTION_DELETE("subscription:delete"),

    PAYMENT_READ("payment:read"),

    ANALYTICS_READ("analytics:read"),

    REPORT_EXPORT("report:export"),

    CURRENCY_READ("currency:read"),

    USER_READ("user:read"),
    USER_UPDATE("user:update"),
    USER_DELETE("user:delete"),

    ADMIN_USER_READ("admin:user:read"),
    ADMIN_USER_UPDATE("admin:user:update"),
    ADMIN_USER_DELETE("admin:user:delete"),

    // Admin tizim bo'yicha statistika (barcha foydalanuvchilar)
    ADMIN_ANALYTICS_READ("admin:analytics:read");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

}
