package com.hvati.administration.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "sales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "sale_number", nullable = false, unique = true)
    private Integer saleNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal total;

    @Column(length = 3)
    private String currency;

    @Column(name = "payment_status", nullable = false)
    private String paymentStatus;

    @Column(name = "delivery_status", nullable = false)
    private String deliveryStatus;

    @Column(name = "sale_date", nullable = false)
    private LocalDateTime saleDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // --- POS / frontend fields (V8) ---
    @Column(precision = 15, scale = 2)
    private BigDecimal subtotal;

    @Column(precision = 15, scale = 2)
    private BigDecimal discount;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "quotation_id")
    private UUID quotationId;

    @Column(name = "quotation_number")
    private Integer quotationNumber;

    @Column(name = "client_name", length = 500)
    private String clientName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "client_address")
    private Map<String, Object> clientAddress;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "shipping_address")
    private Map<String, Object> shippingAddress;

    @Column(length = 50)
    private String channel;

    @Column(name = "register_id")
    private UUID registerId;

    @Column(name = "cashier_id")
    private UUID cashierId;

    @Column(name = "salesperson_id")
    private UUID salespersonId;

    @Column(name = "invoice_id")
    private String invoiceId;

    @Column(name = "invoice_url", length = 500)
    private String invoiceUrl;

    @Column(length = 30)
    private String status;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SaleLineEntity> lines = new ArrayList<>();

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt")
    @Builder.Default
    private List<SaleHistoryEntity> history = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
