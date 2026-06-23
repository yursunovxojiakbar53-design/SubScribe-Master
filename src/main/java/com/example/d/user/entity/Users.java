package com.example.d.user.entity;

import com.example.d.auth.entity.RefreshToken;
import com.example.d.extra.AbstractEntity;
import com.example.d.subscription.entity.Subscription;
import com.example.d.user.enums.CurrencyType;
import com.example.d.user.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class Users extends AbstractEntity {

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    private Integer emailCode;

    private boolean enabled = true;

    private boolean accountNonLocked = true;

    private String fullName;

    @Enumerated(EnumType.STRING)
    private CurrencyType preferredCurrency = CurrencyType.UZS;

    private boolean notificationEnabled = true;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Subscription> subscriptions;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<RefreshToken> refreshTokens;

}
