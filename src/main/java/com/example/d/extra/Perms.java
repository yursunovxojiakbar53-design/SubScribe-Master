package com.example.d.extra;

public final class Perms {

    // ── USER (self-service)
    public static final String SUBSCRIPTION_CREATE = "subscription:create";
    public static final String SUBSCRIPTION_READ = "subscription:read";
    public static final String SUBSCRIPTION_UPDATE = "subscription:update";
    public static final String SUBSCRIPTION_DELETE = "subscription:delete";

    public static final String PAYMENT_READ = "payment:read";
    public static final String ANALYTICS_READ = "analytics:read";
    public static final String REPORT_EXPORT = "report:export";
    public static final String CURRENCY_READ = "currency:read";

    public static final String USER_READ = "user:read";
    public static final String USER_UPDATE = "user:update";
    public static final String USER_DELETE = "user:delete";

    // ── ADMIN ─────────────────────────────────────────
    public static final String ADMIN_USER_READ = "admin:user:read";
    public static final String ADMIN_USER_UPDATE = "admin:user:update";
    public static final String ADMIN_USER_DELETE = "admin:user:delete";
    public static final String ADMIN_ANALYTICS_READ = "admin:analytics:read";

    private Perms() {
    }
}
