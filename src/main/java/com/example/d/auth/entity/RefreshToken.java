package com.example.d.auth.entity;

import com.example.d.extra.AbstractEntity;
import com.example.d.user.entity.Users;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class RefreshToken extends AbstractEntity {

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private boolean revoked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    public RefreshToken() {
    }

    public RefreshToken(String token, LocalDateTime expiryDate, boolean revoked, Users user) {
        this.token = token;
        this.expiryDate = expiryDate;
        this.revoked = revoked;
        this.user = user;
    }

    private RefreshToken(Builder builder) {
        this.token = builder.token;
        this.expiryDate = builder.expiryDate;
        this.revoked = builder.revoked;
        this.user = builder.user;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RefreshToken that = (RefreshToken) o;
        return revoked == that.revoked &&
                Objects.equals(token, that.token) &&
                Objects.equals(expiryDate, that.expiryDate) &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), token, expiryDate, revoked, user);
    }

    @Override
    public String toString() {
        return "RefreshToken{" +
                "token='" + token + '\'' +
                ", expiryDate=" + expiryDate +
                ", revoked=" + revoked +
                '}';
    }

    public static class Builder {
        private String token;
        private LocalDateTime expiryDate;
        private boolean revoked;
        private Users user;

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder expiryDate(LocalDateTime expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public Builder revoked(boolean revoked) {
            this.revoked = revoked;
            return this;
        }

        public Builder user(Users user) {
            this.user = user;
            return this;
        }

        public RefreshToken build() {
            return new RefreshToken(this);
        }
    }
}