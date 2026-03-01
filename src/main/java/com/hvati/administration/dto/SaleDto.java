package com.hvati.administration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SaleDto {
    private UUID id;

    @JsonProperty("number")
    private Integer saleNumber;

    private String status; // pendiente, por_enviar, finalizada, cancelled
    private List<SaleLineDto> lines;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal total;
    private String currency;
    private List<SaleHistoryEntryDto> history;

    @JsonProperty("quotationId")
    private UUID quotationId;

    @JsonProperty("quotationNumber")
    private Integer quotationNumber;

    @JsonProperty("createdAt")
    private String createdAt;

    private String channel;
    private String channelLabel;
    private UUID registerId;
    private String registerName;
    private UUID cashierId;
    private String cashierName;
    private UUID salespersonId;
    private String salespersonName;

    @JsonProperty("clientId")
    private UUID clientId;

    private String clientName;
    private String clientEmail;
    private String clientPhone;
    private SaleAddressDto clientAddress;
    private SaleAddressDto shippingAddress;
    private String invoiceId;
    private String invoiceUrl;
    private String paymentMethod;
}
