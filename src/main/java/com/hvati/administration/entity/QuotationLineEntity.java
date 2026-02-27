package com.hvati.administration.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "quotation_lines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuotationLineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quotation_id", nullable = false)
    private QuotationEntity quotation;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "product_name", nullable = false, length = 500)
    private String productName;

    @Column(name = "is_shipping", nullable = false)
    @Builder.Default
    private Boolean isShipping = false;

    @Column(name = "unit_price", nullable = false, precision = 15, scale = 4)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 12, scale = 4)
    @Builder.Default
    private BigDecimal quantity = BigDecimal.ONE;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "price_list_id")
    private UUID priceListId;

    @Column(name = "price_list_name")
    private String priceListName;
}
