package com.hvati.administration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductLocationStockDto {
    private UUID id;
    private UUID locationId;
    private String locationName;
    private Integer quantity;
    private Integer minQuantity;
}
