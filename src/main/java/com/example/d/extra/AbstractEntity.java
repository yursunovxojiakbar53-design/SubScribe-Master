package com.example.d.extra;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@MappedSuperclass
public class AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


    @Version
    private Long version;

    public AbstractEntity() {
    }

    public AbstractEntity(Integer id, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    protected AbstractEntity(Builder<?, ?> builder) {
        this.id = builder.id;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ENG MUHIM QISM: Generics aniq belgilandi
    public static abstract class Builder<C extends AbstractEntity, B extends Builder<C, B>> {
        private Integer id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        @SuppressWarnings("unchecked")
        protected B self() {
            return (B) this;
        }

        public B id(Integer id) {
            this.id = id;
            return self();
        }

        public B createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return self();
        }

        public B updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return self();
        }

        public abstract C build();
    }
}