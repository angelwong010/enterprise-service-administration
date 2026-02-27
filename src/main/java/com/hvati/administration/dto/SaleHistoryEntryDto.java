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
public class SaleHistoryEntryDto {
    private UUID id;
    private String type;
    private UUID userId;
    private String userName;
    private UUID locationId;
    private String locationName;
    private BigDecimal amount;
    private String comment;
    private Integer quotationNumber;
    private String paymentMethod;
    private String createdAt;
}
