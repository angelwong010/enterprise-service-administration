package com.hvati.administration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductVariantDto {
    private UUID id;
    private String sku;
    private Integer quantity;
    private List<ProductVariantOptionDto> options;
}
