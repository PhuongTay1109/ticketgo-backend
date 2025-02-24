package com.ticketgo.entity;

import com.ticketgo.request.CustomerRegistrationRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private boolean isDeleted;

    protected BaseEntity() {}

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeleted = false;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Mapper
    public static interface CustomerMapper {

        CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

        @Mapping(target = "password", expression = "java(passwordEncoder.encode(request.getPassword()))")
        @Mapping(target = "imageUrl", constant = "https://res.cloudinary.com/dj1h07rea/image/upload/v1728906155/sbcf-default-avatar_iovbch.webp")
        @Mapping(target = "isEnabled", constant = "false")
        @Mapping(target = "isLocked", constant = "false")
        @Mapping(target = "role", expression = "java(com.ticketgo.enums.Role.ROLE_CUSTOMER)")
        @Mapping(target = "provider", expression = "java(com.ticketgo.enums.Provider.LOCAL)")
        Customer toCustomer(CustomerRegistrationRequest request, PasswordEncoder passwordEncoder);
    }
}
