package com.hvati.administration.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String sku;

    private String barcode;

    @Column(name = "product_type", nullable = false)
    private String productType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private BrandEntity brand;

    @Column(name = "unit_of_sale")
    private String unitOfSale;

    @Column(name = "location_text")
    private String locationText;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "use_stock")
    private Boolean useStock;

    @Column(name = "use_lots_expiry")
    private Boolean useLotsExpiry;

    @Column(name = "charge_tax")
    private Boolean chargeTax;

    @Column(name = "iva_rate", precision = 5, scale = 2)
    private BigDecimal ivaRate;

    @Column(name = "ieps_rate", precision = 5, scale = 2)
    private BigDecimal iepsRate;

    @Column(name = "cost_net", precision = 15, scale = 2)
    private BigDecimal costNet;

    @Column(name = "cost_with_tax", precision = 15, scale = 2)
    private BigDecimal costWithTax;

    @Column(name = "include_in_catalog")
    private Boolean includeInCatalog;

    @Column(name = "sell_at_pos")
    private Boolean sellAtPos;

    @Column(name = "require_prescription")
    private Boolean requirePrescription;

    @Column(name = "allow_manufacturing")
    private Boolean allowManufacturing;

    @Column(name = "sat_key")
    private String satKey;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductPriceEntity> prices = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductLocationStockEntity> locationStocks = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductVariantEntity> variants = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
