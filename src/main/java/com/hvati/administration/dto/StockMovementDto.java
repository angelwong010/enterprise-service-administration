package com.hvati.administration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockMovementDto {
    private UUID id;
    private UUID productId;
    private UUID locationId;
    private String locationName;
    private String movementType;
    private Integer quantity;
    private Integer previousQuantity;
    private Integer newQuantity;
    private String referenceType;
    private UUID referenceId;
    private String userId;
    private LocalDateTime createdAt;
}
