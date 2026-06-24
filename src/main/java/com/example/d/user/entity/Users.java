package com.example.d.user.entity;

import com.example.d.auth.entity.RefreshToken;
import com.example.d.extra.AbstractEntity;
import com.example.d.subscription.entity.Subscription;
import com.example.d.subscription.enums.CurrencyType;
import com.example.d.user.enums.Role;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
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

    public Users() {
    }

    public Users(String username, String email, String password, Role role, Integer emailCode,
                 boolean enabled, boolean accountNonLocked, String fullName,
                 CurrencyType preferredCurrency, boolean notificationEnabled,
                 List<Subscription> subscriptions, List<RefreshToken> refreshTokens) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.emailCode = emailCode;
        this.enabled = enabled;
        this.accountNonLocked = accountNonLocked;
        this.fullName = fullName;
        this.preferredCurrency = preferredCurrency;
        this.notificationEnabled = notificationEnabled;
        this.subscriptions = subscriptions;
        this.refreshTokens = refreshTokens;
    }

    protected Users(Builder builder) {
        super(builder);
        this.username = builder.username;
        this.email = builder.email;
        this.password = builder.password;
        this.role = builder.role;
        this.emailCode = builder.emailCode;
        this.enabled = builder.enabled;
        this.accountNonLocked = builder.accountNonLocked;
        this.fullName = builder.fullName;
        this.preferredCurrency = builder.preferredCurrency;
        this.notificationEnabled = builder.notificationEnabled;
        this.subscriptions = builder.subscriptions;
        this.refreshTokens = builder.refreshTokens;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Integer getEmailCode() {
        return emailCode;
    }

    public void setEmailCode(Integer emailCode) {
        this.emailCode = emailCode;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public CurrencyType getPreferredCurrency() {
        return preferredCurrency;
    }

    public void setPreferredCurrency(CurrencyType preferredCurrency) {
        this.preferredCurrency = preferredCurrency;
    }

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public List<RefreshToken> getRefreshTokens() {
        return refreshTokens;
    }

    public void setRefreshTokens(List<RefreshToken> refreshTokens) {
        this.refreshTokens = refreshTokens;
    }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + getId() +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", emailCode=" + emailCode +
                ", enabled=" + enabled +
                ", accountNonLocked=" + accountNonLocked +
                ", fullName='" + fullName + '\'' +
                ", preferredCurrency=" + preferredCurrency +
                ", notificationEnabled=" + notificationEnabled +
                '}';
    }

    public static class Builder extends AbstractEntity.Builder<Users, Builder> {
        private String username;
        private String email;
        private String password;
        private Role role = Role.USER;
        private Integer emailCode;
        private boolean enabled = true;
        private boolean accountNonLocked = true;
        private String fullName;
        private CurrencyType preferredCurrency = CurrencyType.UZS;
        private boolean notificationEnabled = true;
        private List<Subscription> subscriptions;
        private List<RefreshToken> refreshTokens;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public Builder emailCode(Integer emailCode) {
            this.emailCode = emailCode;
            return this;
        }

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder accountNonLocked(boolean accountNonLocked) {
            this.accountNonLocked = accountNonLocked;
            return this;
        }

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder preferredCurrency(CurrencyType preferredCurrency) {
            this.preferredCurrency = preferredCurrency;
            return this;
        }

        public Builder notificationEnabled(boolean notificationEnabled) {
            this.notificationEnabled = notificationEnabled;
            return this;
        }

        public Builder subscriptions(List<Subscription> subscriptions) {
            this.subscriptions = subscriptions;
            return this;
        }

        public Builder refreshTokens(List<RefreshToken> refreshTokens) {
            this.refreshTokens = refreshTokens;
            return this;
        }

        @Override
        public Users build() {
            return new Users(this);
        }
    }
}