package com.hvati.administration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
    @JsonDeserialize(using = FlexibleBigDecimalDeserializer.class)
    private BigDecimal subtotal;
    @JsonDeserialize(using = FlexibleBigDecimalDeserializer.class)
    private BigDecimal discount;
    @JsonDeserialize(using = FlexibleBigDecimalDeserializer.class)
    private BigDecimal total;
    private String status; // draft, sent, accepted, rejected

    @JsonProperty("createdAt")
    private String createdAt;

    @JsonProperty("updatedAt")
    private String updatedAt;

    @JsonProperty("saleId")
    private UUID saleId;
}
