package com.hvati.administration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductPriceItemDto {
    private UUID id;
    private UUID priceListId;
    private String priceListName;
    private BigDecimal price;
    private String currency;
    private BigDecimal marginPercent;
    private BigDecimal profit;
}
