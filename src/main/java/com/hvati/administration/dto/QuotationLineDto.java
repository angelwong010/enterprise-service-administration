package com.hvati.administration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuotationLineDto {
    private UUID id;
    private String productName;
    private UUID productId;

    @JsonProperty("isShipping")
    private Boolean isShipping;

    private BigDecimal unitPrice;
    private BigDecimal quantity;
    private BigDecimal subtotal;
    private String priceListName;
    private UUID priceListId;
}
