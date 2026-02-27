package com.hvati.administration.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sale_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", nullable = false)
    private SaleEntity sale;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "quotation_number")
    private Integer quotationNumber;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
