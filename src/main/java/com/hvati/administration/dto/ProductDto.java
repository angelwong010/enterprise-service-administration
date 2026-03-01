package com.hvati.administration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDto {
    private UUID id;

    @NotBlank(message = "El campo nombre es requerido y no fue enviado. Debe completarse.")
    @Size(min = 1)
    private String name;

    private String sku;
    private String barcode;
    private String productType;

    @NotNull(message = "El campo categoría es requerido y no fue enviado. Debe completarse.")
    private UUID categoryId;

    private String categoryName;

    @NotNull(message = "El campo marca es requerido y no fue enviado. Debe completarse.")
    private UUID brandId;

    private String brandName;

    @NotBlank(message = "El campo unidad de venta es requerido y no fue enviado. Debe completarse.")
    @Size(min = 1)
    private String unitOfSale;
    private String locationText;
    private String description;
    private Boolean useStock;
    private Boolean useLotsExpiry;
    private Boolean chargeTax;

    @DecimalMin(value = "0", message = "El IVA debe ser mayor o igual a 0.")
    @DecimalMax(value = "100", message = "El IVA debe ser menor o igual a 100.")
    private BigDecimal ivaRate;

    private BigDecimal iepsRate;

    @NotNull(message = "El campo costo neto es requerido y no fue enviado. Debe completarse.")
    @DecimalMin(value = "0", message = "El costo neto debe ser mayor o igual a 0.")
    private BigDecimal costNet;

    private BigDecimal costWithTax;
    private Boolean includeInCatalog;
    private Boolean sellAtPos;
    private Boolean requirePrescription;
    private Boolean allowManufacturing;
    private String satKey;

    private Integer totalStock;
    private Integer variantCount;
    private BigDecimal displayPrice;

    private List<ProductPriceItemDto> prices;
    private List<ProductLocationStockDto> locationStocks;
    private List<ProductVariantDto> variants;
    private List<StockMovementDto> lastMovements;
}
