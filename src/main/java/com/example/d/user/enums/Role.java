package com.example.d.user.enums;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.d.user.enums.Permission.*;

@Getter
public enum Role {

    USER(Set.of(
            SUBSCRIPTION_CREATE,
            SUBSCRIPTION_READ,
            SUBSCRIPTION_UPDATE,
            SUBSCRIPTION_DELETE,
            PAYMENT_READ,
            ANALYTICS_READ,
            REPORT_EXPORT,
            CURRENCY_READ,
            USER_READ,
            USER_UPDATE,
            USER_DELETE
    )),

    ADMIN(Set.of(
            SUBSCRIPTION_CREATE,
            SUBSCRIPTION_READ,
            SUBSCRIPTION_UPDATE,
            SUBSCRIPTION_DELETE,
            PAYMENT_READ,
            ANALYTICS_READ,
            REPORT_EXPORT,
            CURRENCY_READ,
            USER_READ,
            USER_UPDATE,
            USER_DELETE,
            ADMIN_USER_READ,
            ADMIN_USER_UPDATE,
            ADMIN_USER_DELETE,
            ADMIN_ANALYTICS_READ
    ));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    /**
     * Role permissions + the {@code ROLE_<name>} authority, ready to be plugged
     * into {@code CustomUserDetails#getAuthorities()} or the JWT token.
     */
    public List<SimpleGrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + name()));
        return authorities;
    }
}
