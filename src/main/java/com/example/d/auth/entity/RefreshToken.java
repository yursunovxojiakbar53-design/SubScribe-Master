package com.example.d.auth.entity;

import com.example.d.extra.AbstractEntity;
import com.example.d.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
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

}
