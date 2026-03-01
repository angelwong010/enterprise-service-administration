package com.hvati.administration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuotationDto {
    private UUID id;

    @JsonProperty("number")
    private Integer number;

    @JsonProperty("clientId")
    private UUID clientId;

    private String clientName;
    private List<QuotationLineDto> lines;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal total;
    private String status; // draft, sent, accepted, rejected

    @JsonProperty("createdAt")
    private String createdAt;

    @JsonProperty("updatedAt")
    private String updatedAt;

    @JsonProperty("saleId")
    private UUID saleId;
}
