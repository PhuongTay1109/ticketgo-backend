package com.ticketgo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tokens")
public class Token extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tokenId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenType tokenType;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @PrePersist
    public void prePersist() {
        this.expiresAt = this.getCreatedAt().plusDays(1);
    }
}

enum TokenType {
    ACTIVATION,
    RESET_PASSWORD
}
