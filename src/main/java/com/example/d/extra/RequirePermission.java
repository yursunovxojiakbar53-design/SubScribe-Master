package com.example.d.extra;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a controller method as requiring a specific permission authority.
 * The required value should be one of the {@link Perms} constants, e.g.
 * {@code @RequirePermission(Perms.SUBSCRIPTION_READ)}.
 *
 * <p>Enforced by {@code PermissionAspect}: throws {@code ForbiddenException}
 * (HTTP 403) when the current user is missing the authority.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    String value();
}
