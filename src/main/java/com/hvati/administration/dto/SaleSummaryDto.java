package com.hvati.administration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SaleSummaryDto {
    private UUID id;
    private Integer saleNumber;
    private BigDecimal total;
    private String currency;
    private String paymentStatus;
    private String deliveryStatus;
    private LocalDateTime saleDate;
}
